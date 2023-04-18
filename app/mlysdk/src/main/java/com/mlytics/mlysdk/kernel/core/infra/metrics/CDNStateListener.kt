package com.mlytics.mlysdk.kernel.core.infra.metrics

import com.mlytics.mlysdk.kernel.core.servant.MCDNStatsHolder
import com.mlytics.mlysdk.util.Component
import com.mlytics.mlysdk.util.DownloaderState
import com.mlytics.mlysdk.util.HttpDownloader
import com.mlytics.mlysdk.util.HttpDownloaderListener
import com.mlytics.mlysdk.util.Logger

object CDNStateListener : Component(), HttpDownloaderListener {

    override suspend fun activate() {
        this.isActivated = true
        HttpDownloader.listeners[key()] = this
    }

    override suspend fun deactivate() {
        this.isActivated = false
        HttpDownloader.listeners.remove(key())
    }

    fun key(): String {
        return this::class.simpleName!!
    }

    override fun onStateChanged(downloader: HttpDownloader, state: DownloaderState) {
        val uri = downloader.uri
        val host = uri.host
        Logger.debug("onStateChanged $state $uri")

        val s = stateMap[host] ?: newState(host)
        when (state) {
            DownloaderState.Running -> {
                ++s.countTotal
            }
            DownloaderState.Connected -> {
                s.totalConnectTime += downloader.connectTime.stop()
                ++s.countSuccess
            }
            DownloaderState.Cancelled -> {
                ++s.countCancel
            }
            DownloaderState.Failed -> {
                ++s.countFailed
            }
            DownloaderState.Completed -> {
                val length = downloader.readLength ?: downloader.contentLength ?: return
                val time = downloader.downloadTime.stop()
                s.complete(length, time)
            }
            else -> {
                Logger.debug("not implemented state")
            }
        }

    }

    private fun newState(host: String): CDNState {
        val state = CDNState(host)
        stateMap[host] = state
        return state
    }

    var stateMap: MutableMap<String, CDNState> = mutableMapOf()

}

class CDNState(val host: String) {

    companion object {
        val defaultBandwidth = 256
    }

    var countCancel = 0
    var countFailed = 0
    var countSuccess = 0
    var countTotal = 0

    var totalConnectTime = 0L

    var totalLength = 0L
    var totalTime = 0L

    var bandwidth: Int = 0
        get() {
            if (totalTime == 0L) return defaultBandwidth
            return (totalLength / totalTime).toInt()
        }

    var connectTime: Int = 0
        get() {
            if (totalConnectTime == 0L) return 0
            return (totalConnectTime / countSuccess).toInt()
        }

    var meanBandwidth = 0
        get() {
            if (countTotal == 0) return defaultBandwidth
            return (bandwidth * countSuccess / countTotal)
        }

    fun complete(length: Long, time: Long) {
        totalTime += Math.max(time, 1)
        totalLength += length

        updateScore()
    }

    private fun updateScore() {

        MCDNStatsHolder.cdns.values.forEach {
            if (it.domain == this.host) {
                it.meanBandwidth = this.meanBandwidth.toDouble()
                return
            }
        }
    }

    override fun toString(): String {
        return "$countTotal-$countFailed=$meanBandwidth"
    }
}