package com.mlytics.mlysdk.vendor

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mlytics.mlysdk.driver.MLYDriver
import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.util.DateTool
import com.mux.stats.sdk.core.model.CustomData
import com.mux.stats.sdk.core.model.CustomerData
import com.mux.stats.sdk.core.model.CustomerPlayerData
import com.mux.stats.sdk.core.model.CustomerVideoData
import com.mux.stats.sdk.core.model.CustomerViewData
import com.mux.stats.sdk.muxstats.MuxStatsExoPlayer
import com.mux.stats.sdk.muxstats.monitorWithMuxData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MuxComponent {

    companion object {
        private var playerData = CustomerPlayerData().apply {
            playerName = "ExoPlayer"
            playerVersion = MLYDriver.version
            viewerUserId = KernelSettings.client.sessionID
            subPropertyId = KernelSettings.client.orgID
            experimentName = KernelSettings.stream.streamID
            playerInitTime = DateTool.millis()
        }

        fun monitor(
            playerView: StyledPlayerView, origin: String
        ): MuxComponent? {

            val envKey = KernelSettings.muxEnvKey ?: return null

            val mux = MuxComponent()

            val player = playerView.player as ExoPlayer
            val customerData = CustomerData().apply {
                customerVideoData = CustomerVideoData().apply {

                    videoTitle = KernelSettings.stream.streamID
                    videoSeries = KernelSettings.stream.streamID
                    videoIsLive = true
                    videoCdn = KernelSettings.platforms.algorithm_id
                    videoSourceUrl = origin
                }
                customerViewData = CustomerViewData().apply {
                    viewSessionId = KernelSettings.stream.streamID
                }
                customData = CustomData().apply {
                    customData1 = KernelSettings.client.id
                }
            }

            GlobalScope.launch(Dispatchers.Main) {
                mux.stats = player.monitorWithMuxData(
                    context = playerView.context,
                    envKey = envKey,
                    playerView = playerView,
                    customerData = customerData
                )
            }

            return mux
        }

    }

    private var stats: MuxStatsExoPlayer? = null

    fun deactivate() {
        stats?.release()
        stats = null
    }

}