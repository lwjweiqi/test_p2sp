package com.mlytics.mlysdk.driver.pheripheral.player

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mlytics.mlysdk.driver.DriverManager
import com.mlytics.mlysdk.driver.integration.streaming.hls.MediaInfo
import com.mlytics.mlysdk.util.BaseUri
import com.mlytics.mlysdk.vendor.MuxComponent
import java.lang.ref.WeakReference

class MLYExoPlayer(playerView: StyledPlayerView) {

    var view: WeakReference<StyledPlayerView> = WeakReference(playerView)
    var mediaInfo: MediaInfo? = null
    var muxComponent: MuxComponent? = null

    companion object {
        var currentPlayer: MLYExoPlayer? = null

        fun Builder(playerView: StyledPlayerView): ExoPlayer.Builder {

            var player = MLYExoPlayer(playerView)
            currentPlayer = player

            val context = playerView.context
            DriverManager.context = context
            val factory = HlsMediaSource.Factory(
                MLYDataSource.Factory(player)
            )
            val builder = ExoPlayer.Builder(context, factory)
            return builder
        }
    }

    fun load(uri: BaseUri) {
        if (mediaInfo == null) {
            initMediaInfo(uri.uri)
        }
    }

    private fun initMediaInfo(origin: String) {
        
        mediaInfo = MediaInfo(origin)

        view.get()?.let {
            muxComponent = MuxComponent.monitor(it, origin)
        }

        mediaInfo?.joinSwarm()

    }

}