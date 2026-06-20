package com.swilkey.iptv

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        loadChannels()
    }

    private fun loadChannels() {
        progressBar.visibility = android.view.View.VISIBLE
        scope.launch {
            try {
                // Load from USA IPTV-ORG repository
                val channels = parseM3UFromUrl("https://iptv-org.github.io/iptv/countries/us.m3u")
                progressBar.visibility = android.view.View.GONE
                if (channels.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No channels found", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                recyclerView.adapter = ChannelAdapter(channels) { channel ->
                    val i = Intent(this@MainActivity, PlayerActivity::class.java)
                    i.putExtra("streamUrl", channel.url)
                    i.putExtra("title", channel.title)
                    i.putExtra("logo", channel.logo)
                    startActivity(i)
                }
            } catch (e: Exception) {
                progressBar.visibility = android.view.View.GONE
                Toast.makeText(this@MainActivity, "Error loading channels: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
