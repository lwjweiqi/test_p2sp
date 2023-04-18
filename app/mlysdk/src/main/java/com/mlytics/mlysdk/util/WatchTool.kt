package com.mlytics.mlysdk.util

class WatchTool() {
    var startTime: Long = System.currentTimeMillis()
    var endTime: Long = -1
    private var total: Long = -1
    var isStop: Boolean = this.total != -1L

    fun hasElapsedTimeS(second: Long): Boolean {
        return this.elapsedTimeS() >= second
    }

    fun stop(): Long {
        if (total == -1L) {
            this.endTime = System.currentTimeMillis()
            total = this.endTime - this.startTime
        }
        return total
    }

    fun elapsedTimeS(): Long {
        return System.currentTimeMillis() - this.startTime
    }

    fun start() {
        this.startTime = System.currentTimeMillis()
    }

    fun reset() {
        this.start()
    }

}
