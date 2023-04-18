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
import com.google.common.net.HttpHeaders
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.headersContentLength
import java.io.IOException
import java.io.InputStream
import java.io.InterruptedIOException

class ExoDataSource(isNetwork: Boolean) : BaseDataSource(isNetwork), HttpDataSource {

    class Factory : HttpDataSource.Factory {
        private val defaultRequestProperties: HttpDataSource.RequestProperties

        private var transferListener: TransferListener? = null

        init {
            defaultRequestProperties = HttpDataSource.RequestProperties()
        }

        override fun setDefaultRequestProperties(defaultRequestProperties: Map<String, String>): Factory {
            this.defaultRequestProperties.clearAndSet(defaultRequestProperties)
            return this
        }

        override fun createDataSource(): ExoDataSource {
            val dataSource = ExoDataSource(
                true
            )
            if (transferListener != null) {
                dataSource.addTransferListener(transferListener!!)
            }
            return dataSource
        }
    }

    private var requestProperties: HttpDataSource.RequestProperties? = null

    private val defaultRequestProperties: HttpDataSource.RequestProperties? = null

    private var dataSpec: DataSpec? = null

    private var response: Response? = null

    private var responseByteStream: InputStream? = null
    private var opened = false
    private var bytesToRead: Long = 0
    private var bytesRead: Long = 0

    override fun getUri(): Uri? {
        return this.dataSpec?.uri
    }

    override fun getResponseCode(): Int {
        return if (response == null) -1 else response!!.code
    }

    override fun getResponseHeaders(): Map<String, List<String>> {
        return if (response == null) mapOf() else response!!.headers.toMultimap()
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

    companion object {
        var client = OkHttpClient.Builder().build()
    }

    @Throws(HttpDataSourceException::class)
    override fun open(dataSpec: DataSpec): Long {
        this.dataSpec = dataSpec
        bytesRead = 0
        bytesToRead = 0
        transferInitializing(dataSpec)
        val request = makeRequest(dataSpec)
        val call = client.newCall(request)
        this.response = call.execute()
        var body = response!!.body!!
        responseByteStream = body.byteStream()

        val responseCode: Int = response!!.code

        if (!response!!.isSuccessful) {
            if (responseCode == 416) {
                if (dataSpec.position == response!!.headersContentLength()) {
                    opened = true
                    transferStarted(dataSpec)
                    return if (dataSpec.length == C.LENGTH_UNSET.toLong()) 0 else dataSpec.length
                }
            }
            closeConnectionQuietly()
            if (responseCode == 416) {
                throw DataSourceException(PlaybackException.ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE)
            }
        }

        val mediaType: MediaType? = body.contentType()
        val bytesToSkip =
            if (responseCode == 200 && dataSpec.position > 0L) dataSpec.position else 0

        bytesToRead = if (dataSpec.length == C.LENGTH_UNSET.toLong()) {
            val contentLength = body.contentLength()
            if (contentLength == -1L) C.LENGTH_UNSET.toLong() else contentLength - bytesToSkip
        } else {
            dataSpec.length
        }
        opened = true
        transferStarted(dataSpec)
        try {
            skipFully(bytesToSkip, dataSpec)
        } catch (e: HttpDataSourceException) {
            closeConnectionQuietly()
            throw e
        }
        return bytesToRead
    }

    @Throws(HttpDataSourceException::class)
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        return try {
            readInternal(buffer, offset, length)
        } catch (e: IOException) {
            throw HttpDataSourceException.createForIOException(
                e, dataSpec!!, HttpDataSourceException.TYPE_READ
            )
        }
    }

    override fun close() {
        if (opened) {
            opened = false
            transferEnded()
            closeConnectionQuietly()
        }
    }

    @Throws(HttpDataSourceException::class)
    private fun makeRequest(dataSpec: DataSpec): Request {
        val position = dataSpec.position
        val length = dataSpec.length

        val builder: Request.Builder = Request.Builder().url(dataSpec.uri.toString())

        val headers: MutableMap<String, String> = mutableMapOf()
        if (defaultRequestProperties != null) {
            headers.putAll(defaultRequestProperties.snapshot)
        }
//        headers.putAll(requestProperties!!.snapshot)
        headers.putAll(dataSpec.httpRequestHeaders)
        for (header in headers) {
            builder.header(header.key, header.value)
        }

        if (position > 0 || length > 0) {
            builder.addHeader(HttpHeaders.RANGE, "bytes=$position-$length")
        }

        if (!dataSpec.isFlagSet(DataSpec.FLAG_ALLOW_GZIP)) {
            builder.addHeader(HttpHeaders.ACCEPT_ENCODING, "identity")
        }

        return builder.build()
    }

    @Throws(HttpDataSourceException::class)
    private fun skipFully(bytesToSkip: Long, dataSpec: DataSpec) {
        var bytesToSkip = bytesToSkip
        if (bytesToSkip == 0L) {
            return
        }

        val skipBuffer = ByteArray(4096)
        try {
            while (bytesToSkip > 0) {
                val read: Int = responseByteStream!!.read(
                    skipBuffer, 0, Integer.min(bytesToSkip.toInt(), skipBuffer.size)
                )
                if (Thread.currentThread().isInterrupted) {
                    throw InterruptedIOException()
                }
                if (read == -1) {
                    throw HttpDataSourceException(
                        dataSpec,
                        PlaybackException.ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE,
                        HttpDataSourceException.TYPE_OPEN
                    )
                }
                bytesToSkip -= read.toLong()
                bytesTransferred(read)
            }
            return
        } catch (e: IOException) {
            if (e is HttpDataSourceException) {
                throw (e as HttpDataSourceException)
            } else {
                throw HttpDataSourceException(
                    dataSpec,
                    PlaybackException.ERROR_CODE_IO_UNSPECIFIED,
                    HttpDataSourceException.TYPE_OPEN
                )
            }
        }
    }

    @Throws(IOException::class)
    private fun readInternal(buffer: ByteArray, offset: Int, readLength: Int): Int {
        var readLength = readLength
        if (readLength == 0) {
            return 0
        }
        if (bytesToRead != C.LENGTH_UNSET.toLong()) {
            val bytesRemaining = bytesToRead - bytesRead
            if (bytesRemaining == 0L) {
                return C.RESULT_END_OF_INPUT
            }
            readLength = Integer.min(readLength, bytesRemaining.toInt())
        }
        val read: Int = responseByteStream!!.read(buffer, offset, readLength)
        if (read == -1) {
            return C.RESULT_END_OF_INPUT
        }
        bytesRead += read.toLong()
        bytesTransferred(read)
        return read
    }

    private fun closeConnectionQuietly() {
        response?.body?.close()
        response = null
        responseByteStream = null
    }

}