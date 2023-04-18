package com.mlytics.mlysdk.util

import java.net.URL

object URLTool {
    fun absoluteURL(base:URL, path: String):URL {
        return URL(base, path)
    }
}

fun URL.portString(): String {
    return if (this.port == -1) "" else ":" + this.port
}

