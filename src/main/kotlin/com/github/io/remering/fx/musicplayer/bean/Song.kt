package com.github.io.remering.fx.musicplayer.bean

import com.fasterxml.jackson.annotation.JsonProperty

data class Song (
    var id: Long,
    var name: String,
    var artists: Array<Artist>,
    var album: Alblum,
    var duration: Int,
    var copyrightId: Long,
    var status: Int,
    var alias: Array<String>,
    var rtype: Int,
    var ftype: Int,
    @field:JsonProperty(value = "transNames", required = false)
    var transNames: Array<String>?,
    var mvid: Long,
    var fee: Int,
    //All field name was lowered
    @field:JsonProperty("rUrl")
    var rUrl: String? = null
){
    constructor(): this(
        0,
        "",
        arrayOf<Artist>(),
        Alblum(),
        0,
        0,
        0,
        arrayOf<String>(),
        0,
        0,
        null,
        0,
        0
    )
}