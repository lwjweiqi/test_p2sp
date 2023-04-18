package com.mlytics.mlysdk.kernel.service.filer.model

import com.mlytics.mlysdk.kernel.core.model.service.Resource
import com.mlytics.mlysdk.util.DownloaderState
import com.mlytics.mlysdk.util.HTTPHeader
import com.mlytics.mlysdk.util.HttpDownloader

class Request(
    var task: HttpDownloader, var resource: Resource
) {

    var isAborted: Boolean = false
        get() {
            return this.task.state == DownloaderState.Cancelled
        }

    suspend fun exit() {
        this.task.cancel()
    }

    suspend fun done() {
        this.task.async()
        this.resource.content = this.task.bytes()
        this.resource.type = this.task.response?.header(HTTPHeader.CONTENT_TYPE) ?: ""
        this.resource.total = this.resource.content?.size
        this.resource.isComplete = this.task.state == DownloaderState.Connected
    }

    suspend fun abort(error: Exception) {
        this.task.cancel()
    }

}
