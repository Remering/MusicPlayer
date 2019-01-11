package com.github.io.remering.fx.musicplayer.utils

import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.concurrent.thread


private val BUFFER by lazy { ByteArrayOutputStream() }
private val WRITER by lazy {
    PrintWriter(BUFFER).apply {
        Runtime.getRuntime().addShutdownHook(thread (false){
            close()
        })
    }
}

fun Logger.severe(t: Throwable) =
    log(Level.SEVERE, t){
        BUFFER.reset()
        t.printStackTrace(WRITER)
        BUFFER.toString()
    }
