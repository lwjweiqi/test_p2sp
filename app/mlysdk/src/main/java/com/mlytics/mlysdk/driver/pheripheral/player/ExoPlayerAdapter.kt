package com.mlytics.mlysdk.driver.pheripheral.player

import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.upstream.BaseDataSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource.HttpDataSourceException
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidContentTypeException
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException
import com.google.android.exoplayer2.upstream.HttpDataSource.RequestProperties
import com.google.android.exoplayer2.upstream.TransferListener
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Util.castNonNull
import com.mlytics.mlysdk.kernel.core.utility.ProxyURLModifier
import java.lang.Integer.min

class ExoPlayerAdapter : Player.Listener {

    fun adapt(player: Player?) {
        player?.addListener(this)
    }

    override fun onEvents(player: Player, events: Player.Events) {
        checkPlayer(player)
    }

    private fun checkMediaItem(item: MediaItem): Uri? {
        val uri = item.localConfiguration?.uri
        if (uri == null) {
            return null
        }
        if (uri.host == "127.0.0.1") {
            return null
        }
        return uri
    }

    fun checkPlayer(player: Player) {
        if (player.mediaItemCount > 0) {
            val item = player.getMediaItemAt(0)
            val uri = checkMediaItem(item)
            if (uri != null) {
                val n = replace(uri)
                player.setMediaItem(n)
                //player.prepare()
                //player.play()
            }
        }
    }

    private fun replace(uri: Uri): MediaItem {
        val url = ProxyURLModifier.replace(uri)!!
        return MediaItem.fromUri(url)
    }
}
