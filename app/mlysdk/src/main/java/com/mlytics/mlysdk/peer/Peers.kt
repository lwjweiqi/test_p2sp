package com.mlytics.mlysdk.peer

import com.google.protobuf.Message
import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.util.FlowID
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.ResourceRequest
import kernel.protocol.client.model.v1.PeerReportResourceStatMessageKt
import kernel.protocol.client.model.v1.messageHeader
import kernel.protocol.client.model.v1.peerReportResourceStatMessage
import java.util.concurrent.locks.ReentrantLock

object Peers {
    var lock = ReentrantLock()
    var peers: MutableMap<String, PeerClient> = mutableMapOf()
    fun register(peer: PeerClient) {
        lock.lock()
        try {
            peers[peer.peerID!!] = peer
        }catch (e: Exception){
            Logger.error("", e)
        } finally {
            lock.unlock()
        }
    }

    fun unregister(peerID: String) {
        lock.lock()
        try {
            peers.remove(peerID)
        }catch (e: Exception){
            Logger.error("", e)
        } finally {
            lock.unlock()
        }

    }

    fun contains(peerID: String): Boolean {
        return peers.contains(peerID)
    }

    fun get(key: String): PeerClient? {
        return peers[key]
    }

    fun reportStat(res: ResourceRequest) {
        Logger.debug("swarm report stat start ${res.uri}")
        val peerID = KernelSettings.client.peerID ?: return
        val swarmID = res.mediaInfo?.swarmID ?: return
        var payload = peerReportResourceStatMessage {
            action = ActionName.MESSAGE_REPORT_RESOURCE_STAT
            header = messageHeader {
                correlationId = FlowID.make()
                sourcePeerId = peerID
            }
            content = PeerReportResourceStatMessageKt.messageContent {
                stat = kernel.protocol.client.model.v1.resourceStat {
                    id = res.id
                    completion = 1
                }
            }
        }
        broadcast(swarmID, payload)
    }

    fun broadcast(swarmID: String, payload: Message) {
        val peer = peers.values.toList().filter {
            it.swarmID == swarmID
        }
        peer.forEach {
            it.send(payload)
        }
    }
}