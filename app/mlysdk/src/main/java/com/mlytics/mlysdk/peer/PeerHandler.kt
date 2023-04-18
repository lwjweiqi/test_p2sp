package com.mlytics.mlysdk.peer

import com.google.protobuf.Message
import com.google.protobuf.kotlin.toByteString
import com.mlytics.mlysdk.kernel.core.model.service.ResourceRange
import com.mlytics.mlysdk.util.ResourceRequest
import kernel.protocol.client.model.v1.Peer
import kernel.protocol.client.model.v1.PeerObtainResourceResponseKt
import kernel.protocol.client.model.v1.peerObtainResourceResponse
import kernel.protocol.client.model.v1.responseHeader

fun Message.onReceive(client: PeerClient) {
    when (this) {
        is Peer.PeerObtainResourceResponse -> this.onReceive(client)
        is Peer.PeerObtainResourceRequest -> this.onReceive(client)
        is Peer.PeerReportResourceStatMessage -> this.onReceive(client)
        is Peer.PeerReportResourceStatsMessage -> this.onReceive(client)
    }
}

fun Peer.PeerObtainResourceResponse.onReceive(client: PeerClient) {

    val resourceID = this.content.id
    val resource = ResourceRequest.cache.get(resourceID) ?: return

    var range = ResourceRange()
    range.start = this.content.range.start.toInt()
    range.end = this.content.range.end.toInt()
    range.data = this.content.pieces.toByteArray()

    resource.contentLength = this.content.total
    resource.contentType = this.content.type
    resource.combiner.appendRange(range)
    resource.done()
}

fun Peer.PeerObtainResourceRequest.onReceive(client: PeerClient) {
    val resourceID = this.content.id
    val resource = ResourceRequest.cache.get(resourceID) ?: return
    val start = this@onReceive.content.range.start.toInt()
    val end = this@onReceive.content.range.end.toInt()
    val length = end - start
    var data = ByteArray(length)
    resource.combiner.data.copyInto(data, 0, start, end)

    var payload = peerObtainResourceResponse {
        action = ActionName.RESPOND_OBTAIN_RESOURCE
        header = responseHeader {
            correlationId = this@onReceive.header.correlationId
            responseCode = 1200
            responseStatus = true
            responseMessage = "success"
        }
        content = PeerObtainResourceResponseKt.responseContent {
            id = resourceID
            uri = resource.uri
            type = resource.contentType
            total = resource.contentLength
            range = this@onReceive.content.range
            pieces = data.toByteString()
        }
    }
    client.send(payload)

}

fun Peer.PeerReportResourceStatMessage.onReceive(client: PeerClient) {
    var resources = ResourceStats.resources[this.header.sourcePeerId]
    if (resources == null) {
        resources = mutableMapOf()
        ResourceStats.resources[this.header.sourcePeerId] = resources
    }
    resources[this.content.stat.id] = this.content.stat.completion
}

fun Peer.PeerReportResourceStatsMessage.onReceive(client: PeerClient) {
    var resources = mutableMapOf<String, Long>()
    this.content.statsList.forEach {
        resources[it.id] = it.completion
    }
    ResourceStats.resources[this.header.sourcePeerId] = resources
}
