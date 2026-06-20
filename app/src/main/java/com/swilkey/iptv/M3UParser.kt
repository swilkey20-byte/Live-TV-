package com.swilkey.iptv

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

suspend fun parseM3UFromUrl(m3uUrl: String): List<Channel> = withContext(Dispatchers.IO) {
    val client = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    val request = Request.Builder()
        .url(m3uUrl)
        .header("User-Agent", "IPTV-Player/1.0")
        .build()

    client.newCall(request).execute().use { resp ->
        if (!resp.isSuccessful) throw Exception("HTTP ${resp.code} when fetching playlist")
        val text = resp.body?.string() ?: ""
        parseM3U(text)
    }
}

suspend fun parseM3UFromAsset(context: Context, assetName: String = "channels.m3u"): List<Channel> =
    withContext(Dispatchers.IO) {
        val text = context.assets.open(assetName).bufferedReader().use { it.readText() }
        parseM3U(text)
    }

fun parseM3U(m3uContent: String): List<Channel> {
    val lines = m3uContent.lines()
    val channels = mutableListOf<Channel>()
    var lastTitle: String? = null
    var lastLogo: String? = null

    // Support attribute parsing on the #EXTINF line (e.g., tvg-id, tvg-logo, group-title)
    val attrRegex = Regex("([a-zA-Z0-9\-]+)=\"([^\"]*)\"|([a-zA-Z0-9\-]+)=([^, ]+)")

    for (raw in lines) {
        val line = raw.trim()
        if (line.isEmpty()) continue
        if (line.startsWith("#EXTINF", ignoreCase = true)) {
            // Extract title after the last comma because attributes may contain commas
            val afterComma = line.substringAfterLast(",").trim()
            lastTitle = afterComma

            // Extract tvg-logo if present
            val matches = attrRegex.findAll(line)
            lastLogo = null
            for (m in matches) {
                val key = m.groups[1]?.value ?: m.groups[3]?.value
                val value = m.groups[2]?.value ?: m.groups[4]?.value
                if (key != null && value != null) {
                    if (key.equals("tvg-logo", true) || key.equals("logo", true)) {
                        lastLogo = value
                    }
                }
            }

        } else if (!line.startsWith("#")) {
            val url = line
            val title = lastTitle ?: url
            channels.add(Channel(title, url, lastLogo))
            lastTitle = null
            lastLogo = null
        }
    }
    return channels
}
