package com.mlytics.mlysdk.util

import com.mlytics.mlysdk.driver.integration.streaming.hls.MediaInfo
import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.core.const.service.WebRTCConstant
import com.mlytics.mlysdk.kernel.core.infra.metrics.CDNStateListener
import com.mlytics.mlysdk.kernel.core.model.service.ResourceRange
import com.mlytics.mlysdk.kernel.core.servant.CDN
import com.mlytics.mlysdk.kernel.core.servant.MCDNStatsHolder
import com.mlytics.mlysdk.peer.ActionName
import com.mlytics.mlysdk.peer.Peers
import com.mlytics.mlysdk.peer.ResourceStats
import kernel.protocol.client.model.v1.PeerObtainResourceRequestKt
import kernel.protocol.client.model.v1.peerObtainResourceRequest
import kernel.protocol.client.model.v1.requestHeader
import kernel.protocol.client.model.v1.resourceRange
import okhttp3.internal.headersContentLength
import java.io.InputStream

class ResourceRequest(val baseUri: BaseUri, val mediaInfo: MediaInfo?) {

    companion object {
        var cache = Cache<ResourceRequest>(128)
    }

    val id: String
    val uri: String
    var priority: Double
    var contentType: String = ""
    var responseCode: Int = 0
    var responseHeaders: Map<String, List<String>> = mapOf()
    var isDone: Boolean = false
    var contentLength: Long = -1
    var combiner = RangeCombiner()
    var cdns: Queue<CDN> = Queue<CDN>()
    var isCDN = false
    var headerReady: AsyncValue<Boolean> = AsyncValue()
    val size: Int
        get() = combiner.data.size
    val isMediaList: Boolean
        get() = this.contentType.endsWith("url", ignoreCase = true)
    val isMediaSegment: Boolean
        get() = this.contentType.isNotEmpty() && !isMediaList

    init {
        this.id = baseUri.id
        this.uri = baseUri.uri
        this.priority = 0.0
    }

    fun close() {
        this.combiner.reset()
        this.downloaders.forEach {
            it.cancel()
        }
    }

    private fun ttl() = 60000L

    fun fromCache(): ResourceRequest? = cache.get(id)
    private fun storeCache() {
        if (this.isMediaSegment) {
            cache.set(id, this, ttl())
        }
    }

    suspend fun from() {

        this.storeCache()

        if (KernelSettings.system.isP2PAllowed) {
            fromPeerCDN()
            return
        }

        if (KernelSettings.system.isMCDNAllowed) {
            fromCDN()
            return
        }
        fromOrigin()
    }

    fun fromOrigin() {
        from(this.uri, baseUri.range)
    }

    private var downloaders = Queue<HttpDownloader>()
    var downloader: HttpDownloader? = null

    fun from(uri: String, range: ResourceRange?) {
        val downloader = HttpDownloader(uri, range = range)
        this.downloader = downloader
        downloaders.append(downloader)
        Logger.debug("dl-- cdn $uri")
        downloader.execute { it ->
            this.header(it)
            if (it.success) {
                it.response?.body?.byteStream()?.let { stream ->
                    try {
                        from(stream)
                    } catch (e: Exception) {
                        Logger.error("dl-- read error", e)
                        this@ResourceRequest.error(e)
                    }
                }
            } else {
                it.response?.body?.close()
                error(it.error)
            }
        }
    }

    fun cancel() {
        while (true) {
            downloaders.removeFirst()?.cancel() ?: break
        }
    }

    fun from(stream: InputStream) {
        val total = if (contentLength > -1) contentLength else Long.MAX_VALUE
        val buffer = ByteArray(16384)
        var read = 0
        while (true) {
            val count = stream.read(buffer)
            if (count == -1) {
                this.contentLength = this.size.toLong()
                Logger.debug("rr-- eof read=$read total=$contentLength $uri")
                break
            }
            val bytes = buffer.copyOfRange(0, count)
            val range = ResourceRange()
            range.data = bytes
            range.start = read
            range.end = read + count
            read = range.end
            this.combiner.appendRange(range)
            if (read >= total) {
                Logger.debug("rr-- total=$total read=$read $uri")
                break
            }
        }
        this.downloader?.complete(read)
        done()
    }

    fun done() {
        isDone = checkDoneBySize()
        Logger.debug("rr-- done $isDone ${this.uri}")
        if (isDone) {
            this.combiner.condition.pass()
            cancel()
            this.downloader = null
            report()
            mediaInfo?.onComplete(this)
        }
    }

    fun report() {
        if (isDone && isMediaSegment) {
            Peers.reportStat(this)
        }
    }

    fun checkDoneBySize(): Boolean {
        var expectLength = baseUri.range?.expendLength ?: this.contentLength.toInt()
        if (expectLength == -1) {
            return false
        }
        return this.size >= expectLength
    }

    var error: Throwable? = null
    fun error(error: Throwable?) {
        error?.let {
            Logger.error("", it)
        }
        if (this.isCDN) {
            fromCDN()
            return
        }
        this.error = error
        done()
    }

    private fun header(downloader: HttpDownloader) {
        downloader.response?.let {
            responseCode = it.code
            responseHeaders = it.headers.toMultimap()
            this.contentLength = it.headersContentLength()
            this.contentType = it.header(HTTPHeader.CONTENT_TYPE, "")!!.lowercase()
        }
        this.headerReady.deliver(true)
    }

    fun initCDNs() {
        if (KernelSettings.debug) {
            if (uri.contains("ovenmediaengine") || uri.contains("vos360")) {
                cdns.append(origin())
                return
            }
        }

        val origin = origin()
        if (this.cdns.isEmpty) {
            val cs = MCDNStatsHolder.cdns.values
            val injectOrigin = cs.find {
                it.domain == origin.domain
            } == null

            var list = cs.sortedWith { a, b ->
                val stats = CDNStateListener.stateMap
                val sa = stats[a.domain!!]
                val sb = stats[b.domain!!]
                if (sa == null && sb == null) {
                    if (a.currentScore == b.currentScore) b.id.hashCode() - a.id.hashCode()
                    else if (a.currentScore < b.currentScore) 1 else -1
                } else {
                    if (sa == null) -1
                    else if (sb == null) 1
                    else if (sa.meanBandwidth < sb.meanBandwidth) 1 else -1
                }
            }.map { it }
            cdns.append(list)
            if (injectOrigin) {
                cdns.append(origin)
            }
        }
    }

    fun origin(): CDN {
        val cdn = CDN()
        cdn.domain = baseUri.url.host
        return cdn
    }

    fun fromCDN() {
        if (this.isDone) {
            return
        }
        isCDN = true
        initCDNs()
        cdns.removeFirst()?.let { cdn ->
            val url = uri(cdn)
            from(url, baseUri.range)
        }
    }

    fun uri(cdn: CDN): String {
        return baseUri.url.run {
            val domain = cdn.domain!!
            val quest = if (query.isNullOrBlank()) "" else "?$query"
            "https://$domain$path$quest"
        }
    }

    fun peerList(): Queue<String> {
        var result = Queue<String>()
        ResourceStats.resources.forEach { peerID, stats ->
            val completion = stats[this.id] ?: 0
            if (completion > 0) {
                result.append(peerID)
            }
        }
        return result
    }

    fun fromPeer(peerID: String): Int {
        if (this.isDone) {
            Logger.debug("webrtc: from peer resource done")
            return 0
        }

        val peer = Peers.get(peerID)
        if (peer == null) {
            Logger.debug("webrtc: from peer peer null")
            return 0
        }

        var size = this.size

        val start = Math.max(size, this.baseUri.range?.start ?: 0).toLong()
        var end = start + WebRTCConstant.DATACHANNEL_CHUNK_SIZE
        if (this.contentLength > -1) {
            end = Math.min(end, this.contentLength)
        }

        var payload = peerObtainResourceRequest {
            action = ActionName.REQUEST_OBTAIN_RESOURCE
            header = requestHeader {
                correlationId = FlowID.make()
                sourcePeerId = KernelSettings.client.peerID!!
            }
            content = PeerObtainResourceRequestKt.requestContent {
                this.id = this@ResourceRequest.id
                this.uri = this@ResourceRequest.uri
                this.range = resourceRange {
                    this.start = start
                    this.end = end
                }
            }
        }
        peer.send(payload)
        return end.toInt() - 1
    }

    suspend fun fromPeers(peers: Queue<String>) {
        if (this.isDone) {
            return
        }

        var peer = peers.removeFirst() ?: return

        while (true) {
            if (this.isDone) {
                return
            }
            val size1 = this.size
            val more = fromPeer(peer)
            if (more > size1) {
                while (true) {
                    val r = this.combiner.more(more)
                    if (r) {
                        break
                    }
                }
            }
            if (this.isDone) {
                return
            }
            val size2 = this.size
            if (size1 == size2) {
                Logger.debug("webrtc: size1=$size1 size2=$size2 size=$size")
                peer = peers.removeFirst() ?: return
            }
        }
    }

    suspend fun fromPeerCDN() {
        if (this.isDone) {
            return
        }
        val peers = peerList()
        if (!peers.isEmpty) {
            fromPeers(peers)
        }

        fromCDN()
    }

    fun completion(): Double {
        if (this.isDone) return 1.0
        if (this.contentLength <= 0) return 0.0
        return this.size.toDouble() / this.contentLength
    }

}