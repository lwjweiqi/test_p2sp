package com.mlytics.mlysdk.driver.pheripheral.player

import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.upstream.BaseDataSource
import com.google.android.exoplayer2.upstream.DataSourceException
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource.HttpDataSourceException
import com.google.android.exoplayer2.upstream.TransferListener
import com.mlytics.mlysdk.driver.DriverManager
import com.mlytics.mlysdk.util.BaseUri
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.ResourceRequest
import com.mlytics.mlysdk.util.WatchTool
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.lang.Integer.min

class MLYDataSource(isNetwork: Boolean) : BaseDataSource(isNetwork), HttpDataSource {

    class Factory(val player: MLYExoPlayer) : HttpDataSource.Factory {
        private val defaultRequestProperties: HttpDataSource.RequestProperties

        private var transferListener: TransferListener? = null

        init {
            defaultRequestProperties = HttpDataSource.RequestProperties()
        }

        override fun setDefaultRequestProperties(defaultRequestProperties: Map<String, String>): Factory {
            this.defaultRequestProperties.clearAndSet(defaultRequestProperties)
            return this
        }

        override fun createDataSource(): MLYDataSource {
            val dataSource = MLYDataSource(
                true
            )
            dataSource.player = player
            if (transferListener != null) {
                dataSource.addTransferListener(transferListener!!)
            }
            return dataSource
        }
    }

    private var requestProperties: HttpDataSource.RequestProperties? = null

    private val defaultRequestProperties: HttpDataSource.RequestProperties? = null

    override fun getUri(): Uri? {
        return this.spec?.uri
    }

    override fun getResponseCode(): Int {
        return resourceRequest?.responseCode ?: 0
    }

    override fun getResponseHeaders(): Map<String, List<String>> {
        return resourceRequest?.responseHeaders ?: mapOf()
    }

    override fun setRequestProperty(name: String, value: String) {
        requestProperties!![name] = value
    }

    override fun clearRequestProperty(name: String) {
        requestProperties!!.remove(name)
    }

    override fun clearAllRequestProperties() {
        requestProperties!!.clear()
    }

    lateinit var player: MLYExoPlayer
    lateinit var spec: DataSpec
    private var opened = false
    private var bytesToRead: Long = 0
    private var bytesRead: Long = 0
    var resourceRequest: ResourceRequest? = null

    override fun open(dataSpec: DataSpec): Long {
        Logger.debug("ds-- open ${dataSpec.uri.toString()}")

        this.spec = dataSpec
        opened = true
        bytesToRead = C.LENGTH_UNSET.toLong()
        bytesRead = 0
        val url = dataSpec.uri.toString()
        val offset = dataSpec.position.toInt()
        val length = dataSpec.length.toInt()
        val uri = BaseUri(url, offset, length)

        player.load(uri)

        var resourceRequest = ResourceRequest(uri, player.mediaInfo)

        transferInitializing(dataSpec)
        val cacheItem = resourceRequest.fromCache()

        if (cacheItem != null) {
            resourceRequest = cacheItem
        }

        this.resourceRequest = resourceRequest
        Logger.debug("ds-- conn ${dataSpec.uri} ${resourceRequest.contentLength}")

        runBlocking(DriverManager.ioScope.coroutineContext) {
            resourceRequest.from()
            if (!resourceRequest.isDone) {
                val job = launch {
                    delay(3000)
                    Logger.debug("ds-- timeout open ${dataSpec.uri}")
                    resourceRequest.headerReady.deny()
                }
                resourceRequest.headerReady.extract()
                job.cancelAndJoin()
            }
        }

        bytesToRead = resourceRequest.contentLength

        transferStarted(dataSpec)
        return bytesToRead
    }

    fun error(e: IOException): HttpDataSourceException {
        return HttpDataSourceException.createForIOException(
            e, spec!!, HttpDataSourceException.TYPE_READ
        )
    }

    @Throws(HttpDataSourceException::class)
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {

        var request = this.resourceRequest!!
        val uri = request.uri
        val read = bytesRead.toInt()
        //Logger.debug("ds-- read=$bytesRead size=${request.size} total=$bytesToRead $uri")
        var readLength = length
        if (readLength == 0) {
            return 0
        }
        if (request.contentLength != -1L) {
            val bytesRemaining = request.contentLength - bytesRead
            if (bytesRemaining <= 0) {
                Logger.debug("ds-- end length ${request.uri}")
                return C.RESULT_END_OF_INPUT
            }
            readLength = min(length, bytesRemaining.toInt())
        }

        if (request.isDone) {
            if (bytesRead >= request.size) {
                Logger.debug("ds-- end done ${request.uri}")
                return C.RESULT_END_OF_INPUT
            }

            readLength = min(readLength, request.size - read)
            if (readLength == 0) {
                return 0
            }

            request.combiner.data.copyInto(buffer, offset, read, read + readLength)

            bytesRead += readLength
            bytesTransferred(readLength)
            Logger.trace("ds-- send read=$bytesRead ${request.uri}")
            return readLength
        }

        runBlocking(DriverManager.ioScope.coroutineContext) {
            val job = launch {
                delay(3000)
                Logger.debug("ds-- timeout read ${request.uri}")
                request.combiner.condition.deny(DataSourceException(PlaybackException.ERROR_CODE_TIMEOUT))
            }
            var startTime = WatchTool()
            request.combiner.more(read + length - 1)
            job.cancelAndJoin()
            //Logger.debug("ds-- size=${request.size} expect=${read + readLength}")
            if (!request.isDone
                && request.combiner.condition.error == null
                && request.size < read + readLength
                && startTime.stop() < 500) {
                delay(500)
                Logger.debug("ds-- delay read ${request.uri}")
            }
        }

        readLength = min(readLength, request.size - read)
        if (readLength == 0) {
            if (request.isDone) {
                Logger.debug("ds-- end done ${request.uri}")
                return C.RESULT_END_OF_INPUT
            }
            throw DataSourceException(PlaybackException.ERROR_CODE_TIMEOUT)
        }
        val len = read + readLength
        request.combiner.data.copyInto(buffer, offset, read, len)

        bytesRead += readLength
        bytesTransferred(readLength)
        Logger.trace("ds-- send=$readLength read=$bytesRead ${request.uri}")
        return readLength
    }

    override fun close() {
        if (opened) {
            opened = false
            try {
                transferEnded()
            } catch (e: Exception) {
                Logger.debug("transferEnded error ${e.message}")
            }
        }
        this.resourceRequest = null
    }

}