package com.mlytics.mlysdk.kernel.core.service.filter.seeker

import com.mlytics.mlysdk.kernel.core.model.service.Resource
import com.mlytics.mlysdk.kernel.service.filer.seeker.DownloaderAgent
import com.mlytics.mlysdk.kernel.service.filer.seeker.base.AbstractDownloader
import com.mlytics.mlysdk.util.Backoff
import com.mlytics.mlysdk.util.ExponentialDelayer
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.WatchTool
import kotlinx.coroutines.delay

abstract class AbstractDownloaderProxy(_resource: Resource) : AbstractDownloader(_resource) {
    override suspend fun fetch() {
        this._setup()
        this.fetch()
    }

    open suspend fun _setup() {
        TODO("AbstractDownloaderProxy not implements _setup")
    }
}

abstract class AbstractFileDownloader(_resource: Resource) : AbstractDownloader(_resource) {
    var _proxy: AbstractDownloaderProxy? = null
    lateinit var _proxyAgent: DownloaderAgent
    var _proxyClasses: MutableList< AbstractDownloaderProxy>? = null

    var watchTool: WatchTool = WatchTool()
    init {
        this._initialize()
    }

    var _allowRetry: Boolean = false
    var _backoff =
        Backoff(ExponentialDelayer(), multiplier = 1.1, maxInterval = 5000)
    var _shouldRetry: Boolean = false
        get() {
            return !this._isAborted && (!this._isQueueEmpty || this._allowRetry)
        }

    var _isQueueEmpty: Boolean = false
        get() {
            return this._proxyClasses!!.isEmpty()
        }

    var _isComplete: Boolean = false
        get() {
            return this._task?.response != null
        }

    var _shouldFetch: Boolean = false
        get() {
            return !this._isAborted && !this._isComplete && !watchTool.hasElapsedTimeS(6)
        }

    abstract fun _initialize()

    override suspend fun _fetch() {
        while (this._shouldFetch) {
            this._ensureQueue()
            this._electProxy()

            this._proxyFetch()
            this._exposeTask()
        }

    }

    override suspend fun _abort() {
        this._proxy?.abort()
    }

    suspend fun _ensureQueue() {
        if (this._proxyClasses != null && this._proxyClasses!!.isEmpty()) {
            delay(100)
            this._proxyClasses = null
        }
        if (this._proxyClasses == null) {
            this._proxyClasses = this._proxyAgent.makeProxyClasses(this._resource)
        }
    }

    suspend fun _electProxy() {
        this._proxy = this._proxyClasses!!.removeFirst()
    }

    suspend fun _proxyFetch() {
        try {
            this._proxy?.fetch()
        } catch (e: Exception) {
            Logger.error("File downloader fetch failed. ${_resource.uri}", e)
            this._proxy?.abort()
            this._proxy = null
            if (!this._shouldRetry) {
                throw e
            }
        }
    }

    fun _exposeTask() {
        this._task = this._proxy?._task
    }

}
