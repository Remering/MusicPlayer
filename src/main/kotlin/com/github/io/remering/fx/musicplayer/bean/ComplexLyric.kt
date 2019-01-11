package com.github.io.remering.fx.musicplayer.bean

import java.time.Duration

private val REGEX = Regex("(\\d{2}):(\\d{2})\\.(\\d{2})") //11:22.33

private fun String.toDuration(): Duration {
    val duration = Duration.ZERO
    with(REGEX.toPattern().matcher(this)){
        duration.plusMinutes(group(0).toLong())
        duration.plusSeconds(group(1).toLong())
        duration.plusMillis(group(2).toLong())
    }
    return duration
}

class ComplexLyric(lyrics: Pair<Lyric, Lyric>){
    val map = HashMap<Duration, Pair<String, String>>()
    init {
        lyrics.first.lyric.lines().zip(lyrics.second.lyric.lines())
            .filter { it.toList().all { it.matches(REGEX) } }
            .forEach {
                val duration = it.first.toDuration()
                val first = it.first.replace(REGEX, "")
                val second = it.second.replace(REGEX, "")
                map[duration] = first to second
            }
    }
}