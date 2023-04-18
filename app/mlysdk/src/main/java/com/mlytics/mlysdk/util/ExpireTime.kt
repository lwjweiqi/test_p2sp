package com.mlytics.mlysdk.util

class ExpireTime(var expireTime: Long = 10000L) {
    fun reset() {
        watch.reset()
    }

    private var watch = WatchTool()
    var isExpired: Boolean = watch.hasElapsedTimeS(expireTime)
}