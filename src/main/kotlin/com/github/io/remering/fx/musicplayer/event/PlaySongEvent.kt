package com.github.io.remering.fx.musicplayer.event

import com.github.io.remering.fx.musicplayer.bean.Song
import tornadofx.FXEvent

class PlaySongEvent(val song: Song): FXEvent()