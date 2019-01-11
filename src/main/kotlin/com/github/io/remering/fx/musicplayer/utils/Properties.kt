package com.github.io.remering.fx.musicplayer.utils

import javafx.beans.property.Property
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.select

fun <T, R> ReadOnlyProperty<T>.readableProperty(trans: (T) -> R, setter: (R) -> Unit): Property<R> {
    return select {
        object : SimpleObjectProperty<R>(){
            override fun get(): R {
                return trans(it)
            }
            override fun set(newValue: R){
                setter(newValue)
            }
        }
    }

}
