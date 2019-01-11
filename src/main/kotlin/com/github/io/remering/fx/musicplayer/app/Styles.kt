package com.github.io.remering.fx.musicplayer.app

import javafx.scene.layout.BackgroundSize
import javafx.scene.paint.Color
import kfoenix.JFXStylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.multi
import tornadofx.px

class Styles: JFXStylesheet() {
    companion object {
        val hideScrollBar by cssclass()
        val smallIconButton by cssclass()
        val bigIconButton by cssclass()
    }
    init {

        root {
            jfxRipplerFill.value = Color.WHITESMOKE
        }

        bigIconButton {
            backgroundRadius += box(40.px)
            borderRadius += box(40.px)
            prefHeight = 80.px
            prefWidth = 80.px

        }


        smallIconButton {
            backgroundRadius += box(20.px)
            borderRadius += box(20.px)
            prefHeight = 40.px
            prefWidth = 40.px
        }


        hideScrollBar {
            jfxListView{
                scrollBar{
                    backgroundColor = multi(Color.TRANSPARENT)
                    backgroundSize = multi(BackgroundSize(0.0, 0.0, false, false, false, false))
                    opacity = 0.0
                    borderColor = multi(box(Color.TRANSPARENT))
                    scaleY = 0
                    scaleX = 0
                    padding = box((-7).px)
                }
            }
        }
    }
}