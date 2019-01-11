package com.github.io.remering.fx.musicplayer.utils

import io.reactivex.Observable
import tornadofx.Component
import tornadofx.FXEvent

inline fun <reified E: FXEvent> Component.rxSubscribe(): Observable<E> =
    Observable.create<E> { emitter ->
        subscribe<E> {
            if (emitter.isDisposed) unsubscribe()
            else emitter.onNext(it)
        }
    }