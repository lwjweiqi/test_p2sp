package com.mlytics.mlysdk.kernel.system

import com.koushikdutta.async.http.server.AsyncHttpServer
import com.koushikdutta.async.http.server.AsyncHttpServerRequest
import com.koushikdutta.async.http.server.AsyncHttpServerResponse
import com.mlytics.mlysdk.driver.DriverManager
import com.mlytics.mlysdk.driver.integration.streaming.hls.HLSLoader
import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.core.const.service.URLContentType
import com.mlytics.mlysdk.kernel.core.model.service.ResourceRange
import com.mlytics.mlysdk.kernel.core.service.filter.CDNOriginKeeper
import com.mlytics.mlysdk.util.Component
import com.mlytics.mlysdk.util.HTTPHeader
import com.mlytics.mlysdk.util.Logger
import com.mlytics.mlysdk.util.MathTool
import kotlinx.coroutines.launch

//import Foundation
//import GCDWebServer
object ProxyComponent : Component() {

    private var usePort: Int = 34567
    private var server: AsyncHttpServer = AsyncHttpServer()

    override suspend fun deactivate() {
        this.server.stop()
        this.isActivated = false
    }

    override suspend fun activate() {
        this.buildServer()
        this.isActivated = true
    }

    private suspend fun buildServer() {
        server.setErrorCallback {
            Logger.error("proxy: start failed", it)
            if (!this.isActivated) {
                return@setErrorCallback
            }
            changePort()
            listen()
        }
        server.get(
            ".*"
        ) { request, response ->
            DriverManager.ioScope.launch {
                handle(request, response)
            }
        }
        this.listen()
    }

    private fun listen() {
        server.listen(usePort)
        KernelSettings.proxy.port = this.usePort
    }

    private fun changePort() {
        this.usePort = 10000 + MathTool.random(65535 - 10000)
    }

    private suspend fun handle(request: AsyncHttpServerRequest, response: AsyncHttpServerResponse) {

        val origin = CDNOriginKeeper.getOrigin(request.url)
        val rangeHeader = request.headers.get(HTTPHeader.RANGE)
        val range = ResourceRange().from(rangeHeader)
        Logger.debug("proxy: origin ${origin}")
        var headers = mutableMapOf<String, String>()
        val res = HLSLoader.load(origin.toString())
        if (res == null) {
            Logger.error("proxy: nil resource")
            return
        }

        val content = res.content
        if (content == null) {
            Logger.error("proxy: nil data")
            return
        }

        if (res.type == null) {
            res.type = URLContentType.from(origin).rawValue
        }

        Logger.debug("proxy: done ${origin} count=${content.size} ")
        response.send(res.type, res.content)
    }

}
