package com.mlytics.example.mlysdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mlytics.mlysdk.driver.MLYDriver
import com.mlytics.mlysdk.driver.pheripheral.player.ExoPlayerAdapter
import com.mlytics.mlysdk.driver.pheripheral.player.MLYExoPlayer

class DemoConfig(val id: String, val server: String, val url: String) {

    companion object {

        var ll = DemoConfig(
            "cgpniujg7i146raqgbdg",
            "vsp.mlytics.com",
            "https://lowlatencydemo.mlytics.com/app/stream/llhls.m3u8"
        )
        var ll2 = DemoConfig(
            "cgpniujg7i146raqgbdg",
            "vsp.mlytics.com",
            "https://lowlatencydemo.mlytics.com/app/stream/abr.m3u8"
        )
        var ll1 = DemoConfig(
            "cgsangrvdp42j9d4c4v0",
            "vsp.mlytics.co",
            "https://lowlatencydemo.mlytics.co/app/stream/llhls.m3u8"
        )
        var ll3 = DemoConfig(
            "cgsangrvdp42j9d4c4v0",
            "vsp.mlytics.co",
            "https://lowlatencydemo.mlytics.co/app/stream/abr.m3u8"
        )
        var prod = DemoConfig(
            "cegh8d9j11u91ba1u600",
            "vsp.mlytics.com",
            "https://vsp-stream.s3.ap-northeast-1.amazonaws.com/HLS/raw/SpaceX.m3u8"
        )
        var uat = DemoConfig(
            "celv6v3lj0nerkd2m3pg",
            "vsp.mlytics.co",
            "https://1001659593134-cloudfront-esmydvcw.xpmon.com/hls/f3c3c1c6-37a3-4c32-af77-ef67ce4656ea.mp4/f3c3c1c6-37a3-4c32-af77-ef67ce4656ea.m3u8"
        )
        var sit = DemoConfig(
            "ceg84gesandb7rct0ke0",
            "vsp.mlytics.us",
            "https://1001635905572-cloudfront-6uqpagm4.svcradar.com/hls/25e6a651-ec4d-4d1f-9664-3cda54acacd9.mp4/25e6a651-ec4d-4d1f-9664-3cda54acacd9.m3u8"
        )
        var default = ll3
    }

}

class MainActivity : AppCompatActivity() {

    private var playerView: StyledPlayerView? = null
    private var playButton: AppCompatButton? = null

    var player: ExoPlayer? = null
    val adapter = ExoPlayerAdapter()

    override fun onDestroy() {
        super.onDestroy()
        MLYDriver.deactivate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerView = findViewById<StyledPlayerView>(R.id.player_view)

        MLYDriver.initialize { options ->
            options.client.id = DemoConfig.default.id
            options.debug = true
            options.server.fqdn = DemoConfig.default.server
        }

        var builder = MLYExoPlayer.Builder(playerView!!)
//        var builder = ExoPlayer.Builder(this)

        val player = builder.build()
        playerView?.player = player
        playerView?.controllerShowTimeoutMs = 0

        playButton = findViewById(R.id.playButton)
        playButton?.setOnClickListener {
            player.setMediaItem(MediaItem.fromUri(DemoConfig.default.url))
            player.playWhenReady = true
            player.prepare()
            player.play()
        }
    }

}
