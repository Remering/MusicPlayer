package com.github.io.remering.fx.musicplayer.view

import com.github.io.remering.fx.musicplayer.app.Styles
import com.github.io.remering.fx.musicplayer.bean.Song
import com.github.io.remering.fx.musicplayer.controller.Music163Controller
import com.github.io.remering.fx.musicplayer.event.SelectedSearchKeywordEvent
import com.github.io.remering.fx.musicplayer.utils.severe
import com.github.thomasnield.rxkotlinfx.events
import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.github.thomasnield.rxkotlinfx.toObservable
import com.jfoenix.controls.JFXPopup
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javafx.collections.ObservableList
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import kfoenix.jfxbutton
import kfoenix.jfxlistview
import kfoenix.jfxpopup
import org.controlsfx.control.textfield.CustomTextField
import tornadofx.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class SongSearchView : View() {



    private val controller by inject<Music163Controller>()

    override val root = CustomTextField().apply {


        var subscription by Delegates.notNull<Disposable>()
        var subscriber by singleAssign<Observer<List<Song>>>()


        left = svgicon("M9.5,3A6.5,6.5 0 0,1 16,9.5C16,11.11 15.41,12.59 14.44,13.73L14.71,14H15.5L20.5,19L19,20.5L14,15.5V14.71L13.73,14.44C12.59,15.41 11.11,16 9.5,16A6.5,6.5 0 0,1 3,9.5A6.5,6.5 0 0,1 9.5,3M9.5,5C7,5 5,7 5,9.5C5,12 7,14 9.5,14C12,14 14,12 14,9.5C14,7 12,5 9.5,5Z"){
            paddingLeft = 5
            paddingRight = 5
        }

        // cross icon
        right = jfxbutton {
            style {
                backgroundRadius += box(20.px)
                borderRadius += box(20.px)
                prefHeight = 40.px
                prefWidth = 40.px
            }
            icon = svgicon("M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z")
            visibleWhen { this@apply.textProperty().isNotEmpty }
            action {
                this@apply.clear()
            }
        }



        var items by singleAssign<ObservableList<Song>>()



        val observable = textProperty()
            .toObservable("")
            .throttleLast(500, TimeUnit.MILLISECONDS)
            .map { it.trim() }
            .filter { !it.isEmpty() }
            .observeOn(Schedulers.io())
//            .doOnNext { println("request") }
            .switchMap { controller.searchSong(it, 5) }
//            .doOnNext { println("get") }
            .doOnError { it.printStackTrace() }
            .observeOnFx()
            .doOnNext { items.clear() }


        jfxpopup {


            this@apply.events(KeyEvent.KEY_PRESSED)
                .filter { it.code == KeyCode.ENTER }
                .doOnNext { if (isShowing) hide() }
                .subscribe { fire(SelectedSearchKeywordEvent(text)) }

            usePrefSize = true
            prefWidthProperty().bind(this@apply.widthProperty())

            events(KeyEvent.KEY_PRESSED)
                .filter { it.code == KeyCode.ENTER }
                .doOnNext { if (isShowing) hide() }
                .subscribe { fire(SelectedSearchKeywordEvent(text)) }
            addClass(Styles.hideScrollBar)

            popupContent = jfxlistview <Song> {

//                isMouseTransparent = true
                isFocusTraversable = true

                events(KeyEvent.KEY_PRESSED)
                    .filter { it.code == KeyCode.ENTER }
                    .doOnNext { this@jfxpopup.hide() }
                    .subscribe { fire(SelectedSearchKeywordEvent(text)) }

                items = this.items

                usePrefSize = true
                prefWidthProperty().bind(this@apply.widthProperty())



                fun convertToString(song: Song) =
                    "${song.name}-${song.artists.joinToString { it.name }}"

                subscriber = object : Observer<List<Song>> {
                    override fun onError(e: Throwable) = log.severe(e)
                    override fun onComplete() = Unit
                    override fun onSubscribe(d: Disposable) {
                        subscription = d
                    }

                    override fun onNext(t: List<Song>) {
                        items.clear()
                        items.addAll(t)
                        this@jfxpopup.show(this@apply,
                            JFXPopup.PopupVPosition.TOP,
                            JFXPopup.PopupHPosition.LEFT,
                            0.0, this@apply.height
                            )
                    }
                }

                cellFormat {
                    text = convertToString(it)
                }


                selectionModel.selectedItemProperty().toObservable()
                    .doOnNext {
                        fire(SelectedSearchKeywordEvent(it.name))
                    }
                    .subscribe {
                        subscription.dispose()
                        this@apply.text = convertToString(it)
                        this@jfxpopup.hide()
                        this@apply.textProperty().onChangeOnce {
                            if (subscription.isDisposed)
                                observable.subscribe(subscriber)
                        }
                    }
            }
        }

        focusedProperty().toObservable()
            .skip(1)
            .subscribe{
                if (it) observable.subscribe(subscriber)
                else subscription.dispose()
            }
    }
}



