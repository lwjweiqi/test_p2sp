package com.mlytics.mlysdk.kernel.service.filer.seeker

import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.core.const.service.SystemModeName
import com.mlytics.mlysdk.kernel.core.model.service.Resource
import com.mlytics.mlysdk.kernel.core.service.filter.AbstractDownloaderAgent
import com.mlytics.mlysdk.kernel.core.service.filter.MCDNDownloaderProxy
import com.mlytics.mlysdk.kernel.core.service.filter.seeker.AbstractDownloaderProxy
import com.mlytics.mlysdk.util.DownloadTaskManager
import kotlin.reflect.KClass

object DownloaderProxyName {

    val MCDN = "mcdn"

    val NODE = "node"

    val USER = "user"
}

class NodeDownloaderProxy(_resource: Resource) : AbstractDownloaderProxy(_resource) {
    override suspend fun _fetch() {
        TODO("Not yet implemented")
    }

    override suspend fun _abort() {
        TODO("Not yet implemented")
    }
}

class UserDownloaderProxy(_resource: Resource) : AbstractDownloaderProxy(_resource) {
    override suspend fun _fetch() {
        TODO("Not yet implemented")
    }

    override suspend fun _abort() {
        TODO("Not yet implemented")
    }
}

class DownloaderAgent : AbstractDownloaderAgent() {

    companion object {

        val PROXY_NAMES: MutableMap<String, MutableList<String>> = mutableMapOf(
            Pair(SystemModeName.MCDN_ONLY, mutableListOf(DownloaderProxyName.MCDN)),
            Pair(SystemModeName.P2P_MCDN, mutableListOf(DownloaderProxyName.MCDN)),
            Pair(SystemModeName.P2P_P2S, mutableListOf()),
            Pair(SystemModeName.P2S_ONLY, mutableListOf())
        )

        val PROXY_CLASSES: MutableMap<String, KClass<out AbstractDownloaderProxy>> = mutableMapOf(
            Pair(DownloaderProxyName.MCDN, MCDNDownloaderProxy::class),
            Pair(DownloaderProxyName.NODE, NodeDownloaderProxy::class),
            Pair(DownloaderProxyName.USER, UserDownloaderProxy::class)
        )
    }

    init {
        this._proxyNames = PROXY_NAMES[KernelSettings.system.mode] ?: mutableListOf()
    }

    override fun createProxy(name: String, _resource: Resource): AbstractDownloaderProxy? {
        return when (name) {
            DownloaderProxyName.MCDN -> MCDNDownloaderProxy(_resource)
            DownloaderProxyName.NODE -> NodeDownloaderProxy(_resource)
            DownloaderProxyName.USER -> UserDownloaderProxy(_resource)
            else -> null
        }
    }

}

object DownloadTaskManagerHolder {
    var instance: DownloadTaskManager = DownloadTaskManager()
}
