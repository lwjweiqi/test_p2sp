package com.mlytics.mlysdk.peer

import com.google.protobuf.Message
import com.mlytics.mlysdk.driver.DriverManager
import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.util.Logger
import kernel.protocol.client.model.v1.Tracker
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun Message.onReceive(client: TrackerClient) {
    when (this) {
        is Tracker.TrackerEnableUserResponse -> this.onReceive(client)
        is Tracker.TrackerJoinSwarmResponse -> this.onReceive(client)
        is Tracker.TrackerObtainSwarmUsersResponse -> this.onReceive(client)
        is Tracker.TrackerConnectUserRequest -> this.onReceive(client)
        is Tracker.TrackerDeliverIceCandidateMessage -> this.onReceive(client)
        is Tracker.TrackerLeaveSwarmMessage -> this.onReceive(client)
        is Tracker.TrackerNotifyExceptionMessage -> this.onReceive(client)
        is Tracker.TrackerDeliverExceptionMessage -> this.onReceive(client)
    }
}

fun Tracker.TrackerEnableUserResponse.onReceive(client: TrackerClient) {
    client.ready.deliver(this.header.responseStatus)
}

fun Tracker.TrackerJoinSwarmResponse.onReceive(client: TrackerClient) {
    val swarmID = SwarmUsers.correlations[this.header.correlationId] ?: return
    SwarmUsers.register(swarmID)
}

fun Tracker.TrackerObtainSwarmUsersResponse.onReceive(client: TrackerClient) {
    var users: MutableMap<String, Double> = mutableMapOf()
    this.content.swarmUsersList.forEach {
        val id = it.valuesList[0].stringValue
        val score = it.valuesList[1].numberValue
        users[id] = score
    }
    val swarmID = SwarmUsers.correlations[this.header.correlationId] ?: return
    SwarmUsers.joinedSwarms[swarmID] = users
    SwarmUsers.correlations.remove(this.header.correlationId)
    DriverManager.ioScope.launch {
        connectSwarmUsers(swarmID, users)
    }
}

suspend fun connectSwarmUsers(swarmID: String, users: MutableMap<String, Double>) {
    var list = users.keys.filter {
        if (Peers.peers.containsKey(it)) {
            return@filter false
        }
        if (it == KernelSettings.client.peerID) {
            return@filter false
        }
        return@filter true
    }.shuffled()
    val min = PeerConstant.MAX_USER_CONNECTIONS.coerceAtMost(list.size)
    list.subList(0, min).forEach {
        val peer = PeerClient()
        peer.peerID = it
        peer.swarmID = swarmID
        Logger.debug("centrifuge: job connect user $it start")
        if (peer.createOffer()) {
            Peers.register(peer)
            Logger.debug("centrifuge: job connect user $it success")
        } else {
            Logger.debug("centrifuge: job connect user $it error")
        }
    }
}

fun Tracker.TrackerConnectUserRequest.onReceive(client: TrackerClient) {

    runBlocking {
        if (action == ActionName.REQUEST_CONNECT_USER) {
            val peer = PeerClient()
            peer.answer(this@onReceive)
        }
        if (action == ActionName.RESPOND_CONNECT_USER) {
            val peer = Peers.get(content.peerId) ?: return@runBlocking
            peer.commitAnswer(this@onReceive)
        }
    }

}

fun Tracker.TrackerDeliverIceCandidateMessage.onReceive(client: TrackerClient) {
    val peer = Peers.get(this.header.sourcePeerId) ?: return
    val client = peer.client
    val ice = this.content.ice
    client.addIceCandidate(ice.candidate, ice.sdpMLineIndex.toInt(), ice.sdpMid)
}

fun Tracker.TrackerLeaveSwarmMessage.onReceive(client: TrackerClient) {
    SwarmUsers.unregister(this.content.swarmId)
}

fun Tracker.TrackerNotifyExceptionMessage.onReceive(client: TrackerClient) {
    Logger.error("centrifuge: TrackerNotifyExceptionMessage $this")
}

fun Tracker.TrackerDeliverExceptionMessage.onReceive(client: TrackerClient) {
    Logger.error("centrifuge: TrackerDeliverExceptionMessage $this")

}