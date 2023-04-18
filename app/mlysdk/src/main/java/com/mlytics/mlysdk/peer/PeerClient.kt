package com.mlytics.mlysdk.peer

import com.google.protobuf.Message
import com.mlytics.mlysdk.driver.DriverManager
import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.util.AsyncValue
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.webrtc.WebRTCClient
import kernel.protocol.client.model.v1.Tracker
import kernel.protocol.client.model.v1.TrackerConnectUserRequestKt
import kernel.protocol.client.model.v1.trackerConnectUserRequest
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class PeerClient {
    val client = WebRTCClient()
    var swarmID: String? = null
    var peerID: String? = null

    fun send(proto: Message) {
        client.send(proto)
    }

    init {
        client.onIce = {
            this.onIce(it)
        }
        client.onReceive = { data ->
            val proto = ProtobufCaster.cast(data)
            Logger.debug("webrtc: recv ${peerID} ${proto.logString}")
            proto.onReceive(this)
        }
        client.onClose = {
            this.close()
        }
    }

    suspend fun createOffer():Boolean {
        Logger.debug("webrtc: createOffer $peerID")
        val offer = client.createOffer()
        if(offer != null && sendOffer(offer)) {
            return true
        }
        this.close()
        return false
    }

    private fun sendOffer(offer: SessionDescription): Boolean {
        Logger.debug("webrtc: sendOffer $peerID")
        return TrackerClient.requestConnectUser(swarmID!!, peerID!!, offer.description)
    }

    suspend fun answer(request: Tracker.TrackerConnectUserRequest) {
        this.peerID = request.header.sourcePeerId
        this.swarmID = request.content.swarmId
        Logger.debug("webrtc: answer $peerID")
        val old = Peers.get(peerID!!)
        if (old != null) {
            val selfID = KernelSettings.client.peerID!!
            if (selfID.compareTo(peerID!!) <= 0) {
                Logger.debug("webrtc: answer close older $peerID")
                old.close()
            } else {
                Logger.debug("webrtc: answer ignore $peerID")
                return
            }
        }
        Peers.register(this)
        val offer = SessionDescription(SessionDescription.Type.OFFER, request.content.sdp)
        Logger.debug("webrtc: create answer $peerID")
        val answer = client.commitOfferAndCreateAnswer(offer)
        if (answer == null) {
            Peers.unregister(this.peerID!!)
        } else {
            val payload = trackerConnectUserRequest {
                action = ActionName.RESPOND_CONNECT_USER
                header = kernel.protocol.client.model.v1.requestHeader {
                    correlationId = request.header.correlationId
                    sourcePeerId = KernelSettings.client.peerID!!
                }
                content = TrackerConnectUserRequestKt.requestContent {
                    this.peerId = request.header.sourcePeerId
                    this.swarmId = request.content.swarmId
                    this.sdp = answer?.description ?: ""
                    this.type = "answer"
                }
            }
            TrackerClient.send(payload)
            answerReady.deliver(true)
        }
    }

    suspend fun commitAnswer(response: Tracker.TrackerConnectUserRequest) {
        Logger.debug("webrtc: commit answer $peerID")

        val answer = SessionDescription(SessionDescription.Type.ANSWER, response.content.sdp)
        val success = client.commitAnswer(answer)
        answerReady.deliver(success)
        Logger.debug("webrtc: commit answer $success $peerID")
        if (!success) {
            this.close()
        }
    }

    var isActivated = true
    private val answerReady: AsyncValue<Boolean> = AsyncValue()
    private fun onIce(ice: IceCandidate) {
        DriverManager.ioScope.launch {
            while (isActivated) {
                val r = answerReady.extract() ?: false
                if (r) {
                    break
                }
            }
            val peerID = this@PeerClient.peerID ?: return@launch
            val swarmID = this@PeerClient.swarmID ?: return@launch
            TrackerClient.sendIce(ice, peerID, swarmID)
        }
    }

    fun close() {
        Logger.debug("webrtc: close $peerID")
        this.isActivated = false
        this.client.close()
        Peers.unregister(this.peerID!!)
    }

}