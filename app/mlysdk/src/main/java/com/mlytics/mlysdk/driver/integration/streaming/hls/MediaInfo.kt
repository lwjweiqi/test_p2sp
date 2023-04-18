package com.mlytics.mlysdk.driver.integration.streaming.hls

import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.peer.ActionName
import com.mlytics.mlysdk.peer.Peers
import com.mlytics.mlysdk.peer.TrackerClient
import com.mlytics.mlysdk.util.FlowID
import com.mlytics.mlysdk.util.HashTool
import com.mlytics.mlysdk.util.M3u8Parser
import com.mlytics.mlysdk.util.ResourceRequest
import kernel.protocol.client.model.v1.PeerReportResourceStatsMessageKt
import kernel.protocol.client.model.v1.Resource
import kernel.protocol.client.model.v1.messageHeader
import kernel.protocol.client.model.v1.peerReportResourceStatsMessage

class MediaInfo(val swarmURI: String) {

    companion object {
        var currentMediaInfo: MediaInfo? = null
        var mediaInfos = mutableMapOf<String, MediaInfo>()
    }

    var segmentIDs: MutableMap<String, Boolean> = mutableMapOf()
    val swarmID: String = HashTool.sha256base64url(swarmURI) ?: swarmURI

    init {
        currentMediaInfo = this
        mediaInfos[swarmID] = this
    }

    fun joinSwarm() {
        val swarmScore = genSwarmScore()
        TrackerClient.joinSwarm(swarmID, swarmScore)
    }

    fun leaveSwarm() {
        TrackerClient.requestLeaveSwarm(swarmID)
        mediaInfos.remove(swarmID)
    }

    fun genSwarmScore(): Double {
        val cache = ResourceRequest.cache
        var total = 0.0
        var count = 0
        val keys = segmentIDs.keys
        val list = keys.toList()

        list.forEach {
            val completion = cache.get(it)?.completion() ?: return@forEach
            total += completion
            count += 1
        }
        if (count == 0) {
            return 0.2
        }
        val result = total / count
        if (result > 1) {
            return 1.0
        }
        if (result < 0.2) {
            return 0.2
        }
        return result
    }

    fun onComplete(res: ResourceRequest) {
        if (res.isDone && res.isMediaList) {
            val bytes = res.combiner.data.toByteArray()
            val content = String(bytes)
            val parser = M3u8Parser(this.swarmURI!!)
            val result = parser.parse(content)
            this.segmentIDs.putAll(result)
        }
    }

    fun reportResourceStats() {

        val peerID = KernelSettings.client.peerID ?: return

        val ss: MutableList<Resource.ResourceStat> = mutableListOf()
        segmentIDs.keys.forEach {
            val completion = ResourceRequest.cache.get(it)?.completion() ?: return@forEach
            if (completion > 0) {
                val res = Resource.ResourceStat.newBuilder()
                res.completion = completion.toLong()
                res.id = it
                ss.add(res.build())
            }
        }
        var payload = peerReportResourceStatsMessage {
            action = ActionName.MESSAGE_REPORT_RESOURCE_STATS

            header = messageHeader {
                correlationId = FlowID.make()
                sourcePeerId = peerID
            }
            content = PeerReportResourceStatsMessageKt.messageContent {
                ss.forEach {
                    this.stats.add(it)
                }
            }
        }
        Peers.broadcast(swarmID, payload)
    }
}