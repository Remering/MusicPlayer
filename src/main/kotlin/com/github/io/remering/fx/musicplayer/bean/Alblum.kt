package com.github.io.remering.fx.musicplayer.bean

import com.fasterxml.jackson.annotation.JsonProperty

data class Alblum (
    var id: Int,
    var name: String,
    var artist: Artist,
    var publishTime: Long,
    var size: Int,
    var copyrightId: Int,
    var status: Int,
    @field:JsonProperty("transNames", required = false)
    var transNames: Array<String>?,
    var picId: Long,
    @field:JsonProperty(required = false)
    var alia: Array<String>? = null
){
    constructor(): this(
        0,
        "",
        Artist(),
        0L,
        0,
        0,
        0,
        null,
        0L
    )
}