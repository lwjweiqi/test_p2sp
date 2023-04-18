package com.mlytics.mlysdk.kernel.core.utility

import android.net.Uri
import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.core.service.filter.CDNOriginKeeper

//import Foundation
object ProxyURLModifier {
    fun replace(url: String): String? {
        val uri = Uri.parse(url)
        return replace(uri)
    }

    fun replace(url: Uri?): String? {
        if (url == null) {
            return null
        }
        CDNOriginKeeper.setOrigin(url)
        val _scheme = KernelSettings.proxy.scheme
        val _host = KernelSettings.proxy.host
        val _port = KernelSettings.proxy.port

        return url.run {
            var _query = query?.let {
                "?" + it
            } ?: ""
            "${_scheme}://${_host}:${_port}${path}${_query}"
        }
    }
}
