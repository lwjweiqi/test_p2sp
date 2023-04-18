package com.mlytics.mlysdk.util

import com.mlytics.mlysdk.kernel.core.model.service.ResourceRange
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.headersContentLength
import java.io.IOException
import java.net.URL
import java.time.Duration
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HttpDownloader(
    val url: String,
    var queries: MutableMap<String, Any?> = mutableMapOf(),
    var headers: MutableMap<String, String> = mutableMapOf(),
    var range: ResourceRange? = null
) : Callback {

    companion object {
        var client = OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(5))
            .writeTimeout(Duration.ofSeconds(5)).readTimeout(
                Duration.ofSeconds(10)
            ).build()

        var listeners: MutableMap<String, HttpDownloaderListener> = mutableMapOf()
    }

    var uri: URL = URL(this.url)
    var request: Request? = null
    var call: Call? = null
    var error: IOException? = null
    var response: Response? = null
    var state: DownloaderState = DownloaderState.Initial
    var closure: (HttpDownloader) -> Unit = {}
    var connectTime: WatchTool = WatchTool()
    var downloadTime: WatchTool = WatchTool()
    var contentLength: Long? = null
    var readLength: Long? = null

    val success: Boolean
        get() {
            return this.state == DownloaderState.Connected || this.state == DownloaderState.Completed
        }

    fun cancel() {
        try {
            if (state == DownloaderState.Running || state == DownloaderState.Connected) {
                this.closeBody()
                this.call?.cancel()
                this.onStateChanged(DownloaderState.Cancelled)
            }
        } catch (e: Exception) {
            Logger.error("download-- close error", e)
        }
    }

    private fun buildRequest() {
        range?.header?.let {
            this.headers[HTTPHeader.RANGE] = it
        }
        this.request = HttpTool.build(url, queries, headers)
    }

    fun execute(closure: (HttpDownloader) -> Unit) {
        this.closure = closure

        buildRequest()
        this.connectTime.reset()
        this.call = client.newCall(this.request!!)
        this.onStateChanged(DownloaderState.Running)
        this.call?.enqueue(this)
    }

    override fun onFailure(call: Call, e: IOException) {
        this.closeBody()
        this.connectTime.stop()
        this.downloadTime.stop()
        this.error = e
        this.onStateChanged(DownloaderState.Failed)
        this.continuation?.resumeWithException(e)
        this.continuation = null
        this.closure(this)
    }

    override fun onResponse(call: Call, response: Response) {
        this.connectTime.stop()
//        this.downloadTime.reset()
        this.response = response
        this.error = null
        this.contentLength = response.headersContentLength()
        this.onStateChanged(DownloaderState.Connected)
        this.continuation?.resume(Unit)
        this.continuation = null
        this.closure(this)

    }

    private fun closeBody() {
        try {
            this.response?.body?.close()
        } catch (e: Exception) {
            Logger.error("download: close error", e)
        }
    }

    fun complete(readLength: Int?) {
        this.closeBody()
        this.readLength = readLength?.toLong()
        if (this.state == DownloaderState.Connected) {
            this.downloadTime.stop()
            this.onStateChanged(DownloaderState.Completed)
        }
    }

    fun bytes(): ByteArray? {
        val bytes = response?.body?.bytes()
        this.complete(bytes?.size)
        return bytes
    }

    var continuation: Continuation<Unit>? = null
    suspend fun async() {
        buildRequest()
        this.call = client.newCall(this.request!!)
        this.onStateChanged(DownloaderState.Running)
        return suspendCoroutine {
            this.continuation = it
            this.call?.enqueue(this)
        }
    }

    fun sync() {
        buildRequest()
        this.call = client.newCall(this.request!!)
        this.onStateChanged(DownloaderState.Running)
        this.response = this.call?.execute()
        onStateChanged(if (this.response == null) DownloaderState.Failed else DownloaderState.Connected)
    }

    private fun onStateChanged(state: DownloaderState) {
        if (this.state == state) {
            return
        }
        this.state = state
        listeners.forEach {
            it.value.onStateChanged(this, state)
        }
    }
}

enum class DownloaderState {
    Initial, Running, Connected, Completed, Cancelled, Failed
}

interface HttpDownloaderListener {
    fun onStateChanged(downloader: HttpDownloader, state: DownloaderState)
}

