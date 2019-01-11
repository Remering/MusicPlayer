package com.github.io.remering.fx.musicplayer.view

import com.github.io.remering.fx.musicplayer.event.ApplicationShutdownEvent
import com.jfoenix.controls.JFXDecorator
import javafx.scene.Scene
import tornadofx.View
import tornadofx.borderpane


class MainView : View("MusicPlayer") {
    val searchView by inject<SongSearchView>()
    val playView by inject<PlayView>()
    override val root = borderpane {
        top<SongSearchView>()
        bottom<PlayView>()
        center<SongListView>()
    }

    override fun onDock() {
        val decorator = JFXDecorator(currentStage, root)
        decorator.isCustomMaximize = true
        val scene = Scene(decorator, 1024.0, 850.0)
        currentStage!!.scene = scene
        currentStage!!.minWidth = 950.0
        currentStage!!.minHeight = 750.0
    }

    override fun onUndock() {
        fire(ApplicationShutdownEvent())
    }

}