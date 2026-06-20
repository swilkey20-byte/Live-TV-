package com.swilkey.iptv

import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

class PlayerActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val playerView = findViewById<PlayerView>(R.id.playerView)
        val streamUrl = intent.getStringExtra("streamUrl") ?: return
        val title = intent.getStringExtra("title") ?: "Playing"

        player = ExoPlayer.Builder(this).build().also { exo ->
            playerView.player = exo
            playerView.useController = true
            val mediaItem = MediaItem.fromUri(Uri.parse(streamUrl))
            exo.setMediaItem(mediaItem)
            exo.playWhenReady = true
            exo.prepare()
        }
        supportActionBar?.title = title
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
