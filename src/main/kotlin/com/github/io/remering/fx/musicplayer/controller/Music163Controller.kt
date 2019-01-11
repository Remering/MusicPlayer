package com.github.io.remering.fx.musicplayer.controller

import com.github.io.remering.fx.musicplayer.bean.Lyric
import com.github.io.remering.fx.musicplayer.bean.Song
import com.github.io.remering.fx.musicplayer.event.ApplicationShutdownEvent
import com.github.io.remering.fx.musicplayer.utils.severe
import io.reactivex.Observable
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.ext.web.client.WebClientOptions
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.client.WebClient
import tornadofx.Controller

inline fun <reified T>JsonObject.mapTo()
    = mapTo(T::class.java)

enum class BpsType (private val value: Int) {

    LOW(128000),
    MEDIUM(192000),
    HIGH(32000),
    HIGHEST(999000);

    override fun toString() = "$value"
}

class Music163Controller: Controller() {

    init {
        subscribe<ApplicationShutdownEvent> {
            client.close()
            Vertx.vertx().close()
            System.exit(0)
        }
    }

    val client: WebClient =
        WebClient.create(Vertx.vertx(), WebClientOptions().apply {
            defaultHost = "music.163.com"
            logActivity = true
            isKeepAlive = true
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36"
           })


    fun searchSong(keyWord: String, limit: Int, offset: Int = 0): Observable<List<Song>> =
        client.post( "/api/search/get/web")
            .addQueryParam("csrf_token", "hlpretag=")
            .addQueryParam("hlposttag", "")
            .addQueryParam("s", keyWord)
            .addQueryParam("type", "1")
            .addQueryParam("offset", "$offset")
            .addQueryParam("total", "false")
            .addQueryParam("limit", "$limit")
            .rxSend()
            .toObservable()
            .map { it.bodyAsJsonObject() }
            .filter { it.containsKey("result") }
            .map { it.getJsonObject("result") }
            .filter { it.containsKey("songs") }
            .map { it.getJsonArray("songs") }
//            .filter { !it.isEmpty }
            .map { it.map { (it as JsonObject).mapTo<Song>() } }
            .doOnError { log.severe(it) }

    fun songUrl(songId: Long, bps: BpsType = BpsType.HIGHEST): Observable<String> =
        client.postAbs("https://api.bzqll.com/music/netease/url/")
            .addQueryParam("key", "579621905")
            .addQueryParam("id", "$songId")
            .addQueryParam("br", "$bps")
            .rxSend()
            .toObservable()
            .filter { it.statusCode() == 302 }
            .filter { it.headers().contains("Location") }
            .map { it.headers()["Location"] }

    fun songLyric(songId: Long): Observable<Pair<Lyric, Lyric>> =
        client.get("/api/song/lyric")
        .addQueryParam("os", "pc")
        .addQueryParam("id", "$songId")
        .addQueryParam("lv", "-1")
        .addQueryParam("tv", "-1")
        .rxSend()
        .toObservable()
        .map { it.bodyAsJsonObject() }
        .filter { it.containsKey("lrc") && it.containsKey("tlyric") }
        .map { it.getJsonObject("lrc") to it.getJsonObject("tlyric") }
        .map { it.first.mapTo(Lyric::class.java) to it.second.mapTo(Lyric::class.java) }
        .doOnError { it.printStackTrace() }
}

