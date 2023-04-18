package com.mlytics.mlysdk.peer

import com.google.protobuf.ByteString
import com.google.protobuf.Message
import com.mlytics.mlysdk.driver.MLYError
import kernel.protocol.client.model.v1.Base
import kernel.protocol.client.model.v1.Peer
import kernel.protocol.client.model.v1.PeerObtainResourceResponseKt
import kernel.protocol.client.model.v1.Tracker
import kernel.protocol.client.model.v1.peerObtainResourceResponse

object ProtobufCaster {

    fun cast(data: ByteArray): Message {

        val payload = Base.MessagePayload.parseFrom(data)

        when (payload.action) {
            ActionName.MESSAGE_REPORT_HEALTH -> return Tracker.TrackerReportHealthMessage.parseFrom(
                data
            )
            ActionName.MESSAGE_NOTIFY_EXCEPTION -> return Tracker.TrackerNotifyExceptionMessage.parseFrom(
                data
            )

            ActionName.MESSAGE_CONFIRM_RECEIPT -> return Tracker.TrackerConfirmReceiptMessage.parseFrom(
                data
            )

            ActionName.MESSAGE_DELIVER_EXCEPTION -> return Tracker.TrackerDeliverExceptionMessage.parseFrom(
                data
            )

            ActionName.MESSAGE_DELIVER_ICE_CANDIDATE -> return Tracker.TrackerDeliverIceCandidateMessage.parseFrom(
                data
            )

            ActionName.REQUEST_ENABLE_USER -> return Tracker.TrackerEnableUserRequest.parseFrom(data)

            ActionName.RESPOND_ENABLE_USER -> return Tracker.TrackerEnableUserResponse.parseFrom(
                data
            )

            ActionName.REQUEST_CONNECT_NODE -> return Tracker.TrackerConnectNodeRequest.parseFrom(
                data
            )

            ActionName.RESPOND_CONNECT_NODE -> return Tracker.TrackerConnectNodeResponse.parseFrom(
                data
            )

            ActionName.REQUEST_CONNECT_USER, ActionName.RESPOND_CONNECT_USER -> return Tracker.TrackerConnectUserRequest.parseFrom(
                data
            )

            ActionName.REQUEST_JOIN_SWARM -> return Tracker.TrackerJoinSwarmRequest.parseFrom(data)

            ActionName.RESPOND_JOIN_SWARM -> return Tracker.TrackerJoinSwarmResponse.parseFrom(data)

            ActionName.MESSAGE_REPORT_SWARM_STATS -> return Tracker.TrackerReportSwarmStatsMessage.parseFrom(
                data
            )

            ActionName.REQUEST_OBTAIN_SWARM_USERS -> return Tracker.TrackerObtainSwarmUsersRequest.parseFrom(
                data
            )

            ActionName.RESPOND_OBTAIN_SWARM_USERS -> return Tracker.TrackerObtainSwarmUsersResponse.parseFrom(
                data
            )

            ActionName.MESSAGE_LEAVE_SWARM -> return Tracker.TrackerLeaveSwarmMessage.parseFrom(data)

            ActionName.MESSAGE_REPORT_HEALTH -> return Peer.PeerReportHealthMessage.parseFrom(data)

            ActionName.MESSAGE_NOTIFY_EXCEPTION -> return Peer.PeerNotifyExceptionMessage.parseFrom(
                data
            )

            ActionName.REQUEST_OBTAIN_RESOURCE -> return Peer.PeerObtainResourceRequest.parseFrom(
                data
            )

            ActionName.RESPOND_OBTAIN_RESOURCE -> return Peer.PeerObtainResourceResponse.parseFrom(
                data
            )

            ActionName.MESSAGE_REPORT_RESOURCE_STAT -> return Peer.PeerReportResourceStatMessage.parseFrom(
                data
            )

            ActionName.MESSAGE_REPORT_RESOURCE_STATS -> return Peer.PeerReportResourceStatsMessage.parseFrom(
                data
            )
            else -> throw MLYError.ProtobufCasterError
        }
    }

}

val Message.logString: String
    get() {
        val string = if (this is Peer.PeerObtainResourceResponse) {
            this.removePieces().toString()
        } else {
            this.toString()
        }
        return string.replace(Regex("[\\r\\n]+"), " ")
    }

fun Peer.PeerObtainResourceResponse.removePieces(): Peer.PeerObtainResourceResponse {
    val src = this
    return peerObtainResourceResponse {
        this.action = src.action
        this.header = src.header
        this.content = PeerObtainResourceResponseKt.responseContent {
            this.id = src.content.id
            this.pieces = ByteString.EMPTY
            this.type = src.content.type
            this.range = src.content.range
            this.total = src.content.total
            this.uri = src.content.uri
        }
    }
}
