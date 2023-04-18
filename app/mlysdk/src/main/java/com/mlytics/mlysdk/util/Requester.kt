package  com.mlytics.mlysdk.util

open class Requester(var host: String?, var timeout: Double = 30.0) {

    var prefix: String

    init {
        prefix = host ?: ""
        if (prefix.isNotEmpty() && !prefix.startsWith("http")) {
            prefix = "https://$prefix"
        }
    }

    suspend inline fun <reified T> fetch(
        path: String,
        queries: Map<String, Any?>? = null,
        headers: Map<String, String?>? = null,
        aborter: AbortController? = null,
        timeout: Double? = null
    ): T? {
        val url = "${prefix}${path}"
        if (!url.startsWith("http")) {
            return null
        }
        val task = PrepareCall(url, queries, headers)
        aborter?.task = task
        return task.await().toObject()
    }

}
