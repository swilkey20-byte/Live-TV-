package com.swilkey.iptv

import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import android.widget.Toast

class PlayerActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val playerView = findViewById<PlayerView>(R.id.playerView)
        val streamUrl = intent.getStringExtra("streamUrl") ?: run {
            Toast.makeText(this, "No stream URL provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val title = intent.getStringExtra("title") ?: "Playing"

        // Build a data source factory with a sane user agent and timeouts
        val userAgent = Util.getUserAgent(this, "IPTV-Player")
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(userAgent)
            .setConnectTimeoutMs(15_000)
            .setReadTimeoutMs(30_000)
            .setAllowCrossProtocolRedirects(true)

        val mediaSourceFactory = DefaultMediaSourceFactory(httpDataSourceFactory)

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().also { exo ->
                playerView.player = exo
                playerView.useController = true
                try {
                    val mediaItem = MediaItem.fromUri(Uri.parse(streamUrl))
                    exo.setMediaItem(mediaItem)
                    exo.playWhenReady = true
                    exo.prepare()
                } catch (e: Exception) {
                    Toast.makeText(this, "Playback error: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        supportActionBar?.title = title
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
