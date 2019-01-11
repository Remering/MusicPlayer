package com.github.io.remering.fx.musicplayer.bean

import com.fasterxml.jackson.annotation.JsonProperty

data class Artist(
    var id: Int,
    var name: String,
    var picUrl: String?,
    var alias: Array<String>,
    var albumSize: Int,
    var picId: Long,
    var img1v1Url: String,
    var img1v1: Int,
    var trans: String?,
    @JsonProperty(required = false)
    var alia: Array<String>? = null
){
    constructor():this(0,
        "",
        null,
        arrayOf<String>(),
        0,
        0,
        "",
        0,
        null)
}