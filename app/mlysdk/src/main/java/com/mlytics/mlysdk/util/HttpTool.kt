package com.mlytics.mlysdk.util

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object HttpTool {

    fun build(headers: Map<String, String?>? = null): Headers {
        var builder = Headers.Builder()
        headers?.forEach {
            if (it.value != null) {
                builder.add(it.key, it.value.toString())
            }
        }
        return builder.build()
    }

    fun queries(url: String, queries: Map<String, Any?>? = null): String {
        if (queries == null || queries.isEmpty()) {
            return url
        }
        var q = url.contains("?")
        var sb = StringBuilder().append(url).apply {
            queries.forEach {
                if (it.value != null) {
                    val values = it.value as? List<*>
                    if (values == null) {
                        appendQuery(this, q, it.key, it.value!!)
                        q = true
                    } else {
                        values?.forEach { value ->
                            if (value != null) {
                                appendQuery(this, q, it.key, value)
                                q = true
                            }
                        }
                    }
                }
            }
        }
        return sb.toString()
    }

    private fun appendQuery(sb: StringBuilder, q: Boolean, key: String, value: Any) {
        sb.apply {
            append(if (q) "&" else "?")
            append(key)
            append("=")
            val string = URLEncoder.encode(value.toString(), "utf8")
            append(value)
        }
    }

    fun form(queries: Map<String, Any?>? = null): FormBody {
        var build = FormBody.Builder()
        queries?.forEach {
            if (it.value != null) {
                val list = it as? List<*>
                if (list == null) {
                    build.add(it.key, it.value.toString())
                } else {
                    for (item in list) {
                        if (item != null) {
                            build.add(it.key, item.toString())
                        }
                    }
                }
            }
        }
        return build.build()
    }

    fun build(
        url: String, queries: Map<String, Any?>?, headers: Map<String, String?>? = null
    ): Request {
        val url = HttpTool.queries(url, queries)
        Logger.debug("http-- ${url} headers=$headers")
        var builder = Request.Builder()
        builder.method("GET", null)
        builder.url(url)
        headers?.forEach {
            val value = it.value ?: return@forEach
            builder.header(it.key, value)
        }
        val uri = Uri.parse(url).host?.let {
            builder.header(HTTPHeader.HOST, it)
        }
        val req = builder.build()
        return req
    }

}

class PrepareCall(
    var url: String,
    var queries: Map<String, Any?>? = null,
    var headers: Map<String, String?>? = null,
    var timeout: Long = 30000
) : Cancelable {

    companion object {
        var client = OkHttpClient.Builder().build()
    }

    var call: Call?
    var request: Request
    var response: Response? = null
    var error: Exception? = null
    var isCanceled: Boolean = false
        get() {
            return call?.isCanceled() ?: false
        }

    init {
        this.request = HttpTool.build(url, queries, headers)
        this.call = client.newCall(request)
    }

    override fun cancel() {
        call?.cancel()
    }

    override var isAborted: Boolean
        get() {
            return isCanceled
        }
        set(value) {}

    suspend fun await(): PrepareCall {

//        val timeoutJob = CoroutineScope(Dispatchers.Default).launch {
//            delay(timeout)
//            call?.cancel()
//        }

        suspendCoroutine { continuation ->
            call?.enqueue(object : Callback {
                override fun onFailure(c: Call, e: IOException) {
                    call = null
                    error = e
                    response = null
//                    timeoutJob.cancel()
                    continuation.resume(Unit)
                }

                override fun onResponse(c: Call, resp: Response) {
                    call = null
                    error = null
                    response = resp
//                    timeoutJob.cancel()
                    continuation.resume(Unit)
                }
            })
        }

        return this
    }

    inline fun <reified T> toObject(): T? {
        if (this is T) {
            return this
        }
        if (Unit is T) {
            return Unit
        }
        val type = object : TypeToken<T>() {}.type
        val string = this.response?.body?.string()
        Logger.debug("http-- resp ${this.request.url} ${string}")
        this.response?.body?.close()
        return Gson().fromJson<T>(string, type)
    }

}

