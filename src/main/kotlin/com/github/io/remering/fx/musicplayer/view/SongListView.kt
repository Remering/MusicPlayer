package com.github.io.remering.fx.musicplayer.view

import com.github.io.remering.fx.musicplayer.bean.Song
import com.github.io.remering.fx.musicplayer.controller.Music163Controller
import com.github.io.remering.fx.musicplayer.event.DownloadSongEvent
import com.github.io.remering.fx.musicplayer.event.PlaySongEvent
import com.github.io.remering.fx.musicplayer.event.SelectedSearchKeywordEvent
import com.github.io.remering.fx.musicplayer.utils.rxSubscribe
import com.github.thomasnield.rxkotlinfx.bind
import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.schedulers.Schedulers
import javafx.geometry.Pos
import javafx.scene.control.OverrunStyle
import javafx.scene.control.TableView
import javafx.scene.paint.Color
import kfoenix.jfxbutton
import tornadofx.*

class SongListView : View() {
    private val controller by inject<Music163Controller>()

    override val root = tableview<Song> {

        isTableMenuButtonVisible = false

        columnResizePolicy = TableView.UNCONSTRAINED_RESIZE_POLICY

        itemsProperty().bind(rxSubscribe<SelectedSearchKeywordEvent>()
            .doOnNext { println("Received") }
            .observeOn(Schedulers.io())
            .switchMap { controller.searchSong(it.keyword, 20, 0) }
            .map { it.observable() }
            .observeOnFx()
            .doOnNext { refresh() }
        )
        stylesheet {
            s(".virtual-flow > .scroll-bar:horizontal"){
                padding = box((-7).px)
                opacity = 0.0
            }
        }


        column("Name", Song::name) {
            tableView.widthProperty().onChange { fixedWidth(it * 3 / 5) }
            cellFormat {
                onDoubleClick {
                    selectionModel.select(index)
                    fire(PlaySongEvent(rowItem))
                }

                graphic = hbox {
                    alignment = Pos.BASELINE_LEFT

                    val h = vbox {
                        isFillHeight = true
                        alignment = Pos.TOP_LEFT
                        textOverrun = OverrunStyle.ELLIPSIS
                        label(it)
                        if (rowItem.alias.isNotEmpty())
                            text(rowItem.alias.joinToString(prefix = "(", postfix = ")")) {
                                style {
                                    fill = Color.GRAY
                                }
                            }
                        minWidthProperty().bind(this@cellFormat.widthProperty() * 2 / 3)

                    }

                    hbox {
                        alignment = Pos.CENTER
                        this.prefWidthProperty().bind(
                            this@cellFormat.widthProperty() - h.widthProperty()
                        )
                        visibleWhen { this@cellFormat.hoverProperty() }
                        alignment = Pos.BASELINE_CENTER
                        jfxbutton {
                            icon = svgicon("M8,5.14V19.14L19,12.14L8,5.14Z")
                            style {
                                borderRadius += box(20.px)
                                backgroundRadius += box(20.px)
                                prefHeight = 40.px
                                prefWidth = 40.px
                                ripplerFill = Color.WHITESMOKE
                            }
                            action {
                                selectionModel.select(index)
                                fire(PlaySongEvent(rowItem))
                            }

                        }
                        jfxbutton{
                            icon = svgicon("M5,20H19V18H5M19,9H15V3H9V9H5L12,16L19,9Z")
                            style {
                                borderRadius += box(20.px)
                                backgroundRadius += box(20.px)
                                prefHeight = 40.px
                                prefWidth = 40.px
                                ripplerFill = Color.WHITESMOKE
                            }
                            action {
                                fire(DownloadSongEvent(rowItem))
                            }

                        }
                    }
                }
            }
        }

        column("Artists", Song::artists) {
            tableView.widthProperty().onChange { fixedWidth(it / 5) }

            cellFormat {
                onDoubleClick {
                    selectionModel.select(index)
                    fire(PlaySongEvent(rowItem))
                }
                prefHeight(height)
                text = it.joinToString { it.name }
            }
        }
        column("Album", Song::album) {
            tableView.widthProperty().onChange { fixedWidth(it / 5) }
            cellFormat {
                onDoubleClick {
                    selectionModel.select(index)
                    fire(PlaySongEvent(rowItem))
                }
                prefHeight(height)
                text = it.name
            }
        }
    }
}

