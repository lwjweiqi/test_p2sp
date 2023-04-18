package com.mlytics.mlysdk.peer

import com.google.protobuf.Message
import com.mlytics.mlysdk.centrifuge.CentrifugeClient
import com.mlytics.mlysdk.driver.DriverManager
import com.mlytics.mlysdk.driver.integration.streaming.hls.MediaInfo
import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.system.RenewClientTokenHandler
import com.mlytics.mlysdk.kernel.system.UpdateClientTokenHandler
import com.mlytics.mlysdk.util.AsyncValue
import com.mlytics.mlysdk.util.Component
import com.mlytics.mlysdk.util.FlowID
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.webrtc.IceTool
import com.mlytics.mlysdk.webrtc.WebRTCClient
import io.github.centrifugal.centrifuge.ConnectionTokenEvent
import io.github.centrifugal.centrifuge.ConnectionTokenGetter
import io.github.centrifugal.centrifuge.TokenCallback
import kernel.protocol.client.model.v1.Tracker
import kernel.protocol.client.model.v1.TrackerDeliverIceCandidateMessageKt
import kernel.protocol.client.model.v1.TrackerJoinSwarmRequestKt
import kernel.protocol.client.model.v1.TrackerReportSwarmStatsMessageKt
import kernel.protocol.client.model.v1.messageHeader
import kernel.protocol.client.model.v1.requestHeader
import kernel.protocol.client.model.v1.trackerConnectUserRequest
import kernel.protocol.client.model.v1.trackerDeliverIceCandidateMessage
import kernel.protocol.client.model.v1.trackerEnableUserRequest
import kernel.protocol.client.model.v1.trackerJoinSwarmRequest
import kernel.protocol.client.model.v1.trackerObtainSwarmUsersRequest
import kernel.protocol.client.model.v1.trackerReportHealthMessage
import kernel.protocol.client.model.v1.trackerReportSwarmStatsMessage
import kernel.protocol.client.model.v1.webRTCICE
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.webrtc.IceCandidate

object TrackerClient : Component() {

    lateinit var token: String
    lateinit var peerID: String
    lateinit var url: String
    lateinit var channel: String

    private suspend fun genToken() {
        if (KernelSettings.client.token == null || KernelSettings.client.peerID == null){
            UpdateClientTokenHandler().process()
        }
    }

    override suspend fun activate() {
        genToken()
        token = KernelSettings.client.token ?: return
        peerID = KernelSettings.client.peerID ?: return
        super.activate()
        val host = KernelSettings.server.tracker.fqdns?.random()!!
        url = "wss://$host/centrifugo/connection/websocket"
        channel = "#$peerID"
        client.startup(token, url, channel)
        PeerJob.activate()
        WebRTCClient.createFactory(DriverManager.context)
    }

    override suspend fun deactivate() {
        super.deactivate()
        client.shutdown()
        PeerJob.deactivate()
    }

    fun send(proto: Message) {
        if (!isActivated) {
            return
        }
        client.send(proto)
    }

    private fun requestEnableUser() {
        var request = trackerEnableUserRequest {
            action = ActionName.REQUEST_ENABLE_USER
            header = requestHeader {
                correlationId = FlowID.make()
                sourcePeerId = KernelSettings.client.peerID!!
            }
        }
        send(request)
    }

    val enabled: Boolean get() = ready.data == true

    suspend fun waitEnableUser() {
        while (true) {
            if (ready.extract() == true) {
                return
            }
        }
    }

    fun joinSwarm(swarmID: String, swarmScore: Double) {

        DriverManager.ioScope.launch {

            waitEnableUser()

            var request: Tracker.TrackerJoinSwarmRequest = trackerJoinSwarmRequest {
                action = ActionName.REQUEST_JOIN_SWARM
                header = requestHeader {
                    correlationId = FlowID.make()
                    sourcePeerId = KernelSettings.client.peerID!!
                }

                content = TrackerJoinSwarmRequestKt.requestContent {
                    this.swarmId = swarmID
                    this.swarmScore = swarmScore
                }
                SwarmUsers.correlations[this.header.correlationId] = swarmID
            }
            send(request)

        }

    }

    fun reportSwarmStats(swarmID: String, swarmScore: Double) {
        var request = trackerReportSwarmStatsMessage {
            action = ActionName.MESSAGE_REPORT_SWARM_STATS
            header = messageHeader {
                correlationId = FlowID.make()
                sourcePeerId = KernelSettings.client.peerID!!
            }
            content = TrackerReportSwarmStatsMessageKt.messageContent {
                this.swarmId = swarmID
                this.swarmScore = swarmScore
            }
        }
        send(request)
    }

    fun requestLeaveSwarm(swarmID: String) {
        var request: Tracker.TrackerJoinSwarmRequest = trackerJoinSwarmRequest {
            action = ActionName.REQUEST_JOIN_SWARM
            header = requestHeader {
                correlationId = FlowID.make()
                sourcePeerId = KernelSettings.client.peerID!!
            }

            content = TrackerJoinSwarmRequestKt.requestContent {
                this.swarmId = swarmID
                this.swarmScore = swarmScore
            }
            SwarmUsers.correlations[this.header.correlationId] = swarmID
        }
        send(request)
    }

    fun requestConnectUser(swarmID: String, peerID: String, offer: String): Boolean {

        var request = trackerConnectUserRequest {
            action = ActionName.REQUEST_CONNECT_USER
            header = requestHeader {
                correlationId = FlowID.make()
                sourcePeerId = KernelSettings.client.peerID!!
            }
            content = kernel.protocol.client.model.v1.TrackerConnectUserRequestKt.requestContent {
                this.swarmId = swarmID
                this.peerId = peerID
                this.sdp = offer
                this.type = "offer"
            }
        }
        send(request)
        return true
    }

    fun requestObtainSwarmUsers(swarmID: String) {
        var request = trackerObtainSwarmUsersRequest {
            action = ActionName.REQUEST_OBTAIN_SWARM_USERS
            header = requestHeader {
                correlationId = FlowID.make()
                sourcePeerId = KernelSettings.client.peerID!!
            }
            content =
                kernel.protocol.client.model.v1.TrackerObtainSwarmUsersRequestKt.requestContent {
                    this.swarmId = swarmID
                }
        }
        SwarmUsers.correlations[request.header.correlationId] = swarmID
        send(request)
    }

    fun reportHealth() {
        if (!enabled) {
            return
        }
        var request = trackerReportHealthMessage {
            action = ActionName.MESSAGE_REPORT_HEALTH
            header = messageHeader {
                sourcePeerId = KernelSettings.client.peerID!!
            }
        }
        send(request)
    }

    fun sendIce(ice: IceCandidate, peerID: String, swarmID: String) {
        var payload = trackerDeliverIceCandidateMessage {
            action = ActionName.MESSAGE_DELIVER_ICE_CANDIDATE
            header = requestHeader {
                correlationId = FlowID.make()
                sourcePeerId = KernelSettings.client.peerID!!
            }
            content = TrackerDeliverIceCandidateMessageKt.requestContent {
                val fragment = IceTool.usernameFragment(ice.sdp)
                this.ice = webRTCICE {
                    candidate = ice.sdp
                    sdpMid = ice.sdpMid
                    sdpMLineIndex = ice.sdpMLineIndex.toLong()
                    usernameFragment = fragment
                }
            }
        }
        send(payload)
    }

    var client = CentrifugeClient()
    var ready: AsyncValue<Boolean> = AsyncValue()

    init {

        client.onError = {
            Logger.error("centrifuge: onError", it)
            this.isActivated = false
        }

        client.onSubscribed = {
            Logger.debug("centrifuge: onSubscribed $it")
            if (it == this.channel) {
                this.requestEnableUser()
                MediaInfo.currentMediaInfo?.joinSwarm()
            }
        }

        client.onReceive = {
            val proto = ProtobufCaster.cast(it)
            Logger.debug("centrifuge: recv ${proto.logString}")
            proto.onReceive(this)
        }

        client.tokenGetter = object : ConnectionTokenGetter() {
            override fun getConnectionToken(event: ConnectionTokenEvent?, cb: TokenCallback) {
                Logger.debug("centrifuge: tokenGetter")
                runBlocking {
                    try {
                        RenewClientTokenHandler().process()
                        val token = KernelSettings.client.token!!
                        client.token = token
                        cb.Done(null, token)
                    } catch (e: Exception) {
                        cb.Done(e, null)
                    }
                }
            }
        }

    }

}