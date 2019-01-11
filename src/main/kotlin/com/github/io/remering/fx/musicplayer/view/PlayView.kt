package com.github.io.remering.fx.musicplayer.view

import com.github.io.remering.fx.musicplayer.controller.Music163Controller
import com.github.io.remering.fx.musicplayer.event.PlaySongEvent
import com.github.io.remering.fx.musicplayer.event.ShowLyricEvent
import com.github.io.remering.fx.musicplayer.utils.readableProperty
import com.github.io.remering.fx.musicplayer.utils.rxSubscribe
import com.github.thomasnield.rxkotlinfx.bind
import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.github.thomasnield.rxkotlinfx.toObservable
import com.jfoenix.controls.JFXDecorator
import io.reactivex.schedulers.Schedulers
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaPlayer.Status
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration
import kfoenix.jfxbutton
import kfoenix.jfxslider
import tornadofx.*
import kotlin.math.roundToInt

const val VOLUME_HIGH = "M14,3.23V5.29C16.89,6.15 19,8.83 19,12C19,15.17 16.89,17.84 14,18.7V20.77C18,19.86 21,16.28 21,12C21,7.72 18,4.14 14,3.23M16.5,12C16.5,10.23 15.5,8.71 14,7.97V16C15.5,15.29 16.5,13.76 16.5,12M3,9V15H7L12,20V4L7,9H3Z"
const val VOLUNE_MEDIUM = "M5,9V15H9L14,20V4L9,9M18.5,12C18.5,10.23 17.5,8.71 16,7.97V16C17.5,15.29 18.5,13.76 18.5,12Z"
const val VOLUME_LOW = "M7,9V15H11L16,20V4L11,9H7Z"
const val VOLUME_OFF = "M12,4L9.91,6.09L12,8.18M4.27,3L3,4.27L7.73,9H3V15H7L12,20V13.27L16.25,17.53C15.58,18.04 14.83,18.46 14,18.7V20.77C15.38,20.45 16.63,19.82 17.68,18.96L19.73,21L21,19.73L12,10.73M19,12C19,12.94 18.8,13.82 18.46,14.64L19.97,16.15C20.62,14.91 21,13.5 21,12C21,7.72 18,4.14 14,3.23V5.29C16.89,6.15 19,8.83 19,12M16.5,12C16.5,10.23 15.5,8.71 14,7.97V10.18L16.45,12.63C16.5,12.43 16.5,12.21 16.5,12Z"

const val ICON_LOADING = "M13,2.05V4.05C17.39,4.59 20.5,8.58 19.96,12.97C19.5,16.61 16.64,19.5 13,19.93V21.93C18.5,21.38 22.5,16.5 21.95,11C21.5,6.25 17.73,2.5 13,2.03V2.05M5.67,19.74C7.18,21 9.04,21.79 11,22V20C9.58,19.82 8.23,19.25 7.1,18.37L5.67,19.74M7.1,5.74C8.22,4.84 9.57,4.26 11,4.06V2.06C9.05,2.25 7.19,3 5.67,4.26L7.1,5.74M5.69,7.1L4.26,5.67C3,7.19 2.25,9.04 2.05,11H4.05C4.24,9.58 4.8,8.23 5.69,7.1M4.06,13H2.06C2.26,14.96 3.03,16.81 4.27,18.33L5.69,16.9C4.81,15.77 4.24,14.42 4.06,13M10,16.5L16,12L10,7.5V16.5Z"
const val ICON_PAUSE = "M13,16V8H15V16H13M9,16V8H11V16H9M12,2A10,10 0 0,1 22,12A10,10 0 0,1 12,22A10,10 0 0,1 2,12A10,10 0 0,1 12,2M12,4A8,8 0 0,0 4,12A8,8 0 0,0 12,20A8,8 0 0,0 20,12A8,8 0 0,0 12,4Z"
const val ICON_PLAY = "M12,20C7.59,20 4,16.41 4,12C4,7.59 7.59,4 12,4C16.41,4 20,7.59 20,12C20,16.41 16.41,20 12,20M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M10,16.5L16,12L10,7.5V16.5Z"

class PlayView : View() {

    init {

    }
    private val lyricStage by lazy { Stage().apply {
        initOwner(currentStage)
        val decorator = JFXDecorator(this, find<LyricView>().root)
        decorator.isCustomMaximize = true
        val scene = Scene(decorator, 450.0, 850.0)
        isResizable = false
        this.scene = scene
        x = currentStage!!.x + currentStage!!.width
        y = currentStage!!.y
        currentStage!!.xProperty().onChange { x = it + currentStage!!.width }
        currentStage!!.widthProperty().onChange { x = it + currentStage!!.x }
        currentStage!!.yProperty().onChange { y = it }
    } }

    private val mediaPlayerProperty = SimpleObjectProperty<MediaPlayer>()
    private var mediaPlayer by mediaPlayerProperty
    private val mediaStatusProperty = SimpleObjectProperty<Status>(Status.UNKNOWN)
    private val controller by inject<Music163Controller>()

    override val root = vbox {

        alignment = Pos.CENTER

        mediaPlayerProperty.bind(
            rxSubscribe<PlaySongEvent>()
                .map { it.song }
                .doOnNext { fire(ShowLyricEvent(it)) }
                .observeOn(Schedulers.io())
                .switchMap { controller.songUrl(it.id) }
                .doOnNext { if (mediaPlayer!= null) mediaPlayer.dispose() }
                .map { MediaPlayer(Media(it)) }
                .doOnNext { it.isAutoPlay = true }
                .doOnNext { mediaStatusProperty.bind(it.statusProperty()) }
        )



        val disabledObservable = mediaStatusProperty.toObservable()
            .map { it in arrayOf(Status.UNKNOWN, Status.STALLED, Status.DISPOSED) }


        // Progress slider
        jfxslider {
            disableProperty().bind(disabledObservable)

            useMaxWidth = true

            //Store the last Property which is bindBidirectional with valueProperty(),
            //then you can unbindBidirectional it with valueProperty() before
            //bindBidirectional valueProperty() with a new one.
            //Or something magic(guichu) will happen.
            var temp: Property<Number>? = null

            min = 0.0
            value = 0.0
            mediaPlayerProperty.toObservable()
                .flatMap { it.statusProperty().toObservable() }
                .filter { it == MediaPlayer.Status.READY }
                .subscribe {
                    max = mediaPlayer.totalDuration.toSeconds()
                    temp?.let { valueProperty().unbindBidirectional(temp) }
                    temp = mediaPlayer.currentTimeProperty()
                        .readableProperty<Duration, Number>(
                            trans = { it.toSeconds() },
                            setter = {
                                mediaPlayer.seek(it.seconds)
                            }
                        )
                    valueProperty().bindBidirectional(temp)
                }
        }





        hbox {
            paddingLeft = 20.0
            paddingRight = 20.0
            alignment = Pos.CENTER

            val volumeIconProperty = svgpath().contentProperty()

            jfxslider {
                min = 0.0
                max = 100.0
                value = max
                isDisable = false
                mediaPlayerProperty.toObservable()
                    .subscribe {
                        it.setOnEndOfMedia {
                            it.stop()
                            it.startTime = 0.seconds
                        }
                    }
                mediaPlayerProperty.toObservable()
                    .map { it.volumeProperty() }
                    .subscribe { it.bind(valueProperty() / 100) }
                volumeIconProperty.bind (
                    valueProperty().toObservable()
                        .map { it.toDouble() }
                        .map {
                            when {
                                it > 100.0 / 3 * 2 -> VOLUME_HIGH
                                it > 100.0 / 3 -> VOLUNE_MEDIUM
                                it - 0.0 < Double.MIN_VALUE -> VOLUME_OFF
                                else -> VOLUME_LOW
                            }
                        }
                )
            }

            spacer()

            // Current time
            label {
                textProperty().bind(mediaPlayerProperty.toObservable()
                    .flatMap { it.currentTimeProperty().toObservable(Duration.ZERO) }
                    .observeOnFx()
                    .map { "%d:%02d".format(it.toMinutes().toInt(), (it.toSeconds() % 60).toInt()) }
                )
            }




            jfxbutton {
                style {
                    backgroundRadius += box(35.px)
                    borderRadius += box(35.px)
                    prefHeight = 70.px
                    prefWidth = 70.px
                    ripplerFill = Color.TRANSPARENT
                }
                disableProperty().bind(disabledObservable)

                graphic = svgpath{
                    scaleX = 2.5
                    scaleY = 2.5
                    useMaxSize = true
                    contentProperty().bind(
                        mediaStatusProperty.toObservable(Status.UNKNOWN)
                            .map {
                                when(it){

                                    //play icon
                                    Status.READY, Status.STOPPED, Status.PAUSED -> ICON_PLAY
                                    //pause icon
                                    Status.PLAYING, Status.HALTED -> ICON_PAUSE
                                    //loading icon
                                    else -> ICON_LOADING
                                }
                            }
                    )
                }


                action {
                    if (mediaPlayer.status == MediaPlayer.Status.PLAYING)
                        mediaPlayer.pause()
                    else
                        mediaPlayer.play()
                }

            }

            label {
                textProperty().bind(mediaPlayerProperty.toObservable()
                    .flatMap { it.totalDurationProperty().toObservable(Duration.INDEFINITE) }
                    .map { "%d:%02d".format(it.toMinutes().roundToInt(), (it.toSeconds() % 60).roundToInt()) }
                    .observeOnFx()
                )
            }

            spacer()

            jfxbutton {
                text = "词"
                action {
                    if (lyricStage.isShowing)
                        lyricStage.close()
                    else
                        lyricStage.show()
                }

            }
        }

    }

}
