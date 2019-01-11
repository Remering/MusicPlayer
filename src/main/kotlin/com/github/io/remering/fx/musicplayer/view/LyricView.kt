package com.github.io.remering.fx.musicplayer.view

import com.github.io.remering.fx.musicplayer.bean.Lyric
import com.github.io.remering.fx.musicplayer.controller.Music163Controller
import com.github.io.remering.fx.musicplayer.event.ShowLyricEvent
import com.github.io.remering.fx.musicplayer.utils.rxSubscribe
import com.github.thomasnield.rxkotlinfx.bind
import io.reactivex.schedulers.Schedulers
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import kfoenix.jfxbutton
import tornadofx.*

class LyricView : View() {
    private val controller by inject<Music163Controller>()
    private val lyricsProperty = SimpleObjectProperty<Pair<Lyric, Lyric>>()
    val translateProperty = SimpleBooleanProperty(false)
    var translate by translateProperty

    override val root = vbox {
        val text = textarea {
            isEditable = false
            lyricsProperty.bind(rxSubscribe<ShowLyricEvent>()
                .map { it.song.id }
                .observeOn(Schedulers.io())
                .switchMap { controller.songLyric(it) }
                .doOnError {
                    it.printStackTrace()
                }
            )
            textProperty().bind(
                lyricsProperty.stringBinding(translateProperty){
                    if (it == null) ""
                    else if (translate) it.second.lyric else it.first.lyric
                }
            )
        }
        jfxbutton("译") {
            text.prefHeightProperty().bind(this@vbox.heightProperty() - heightProperty())
            useMaxWidth = true
            action {
                translate = !translate
            }
        }
    }

}
