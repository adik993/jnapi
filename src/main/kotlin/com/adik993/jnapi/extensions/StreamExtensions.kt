package com.adik993.jnapi.extensions

import io.reactivex.Observable
import java.util.stream.Stream

fun <T : Any?> Stream<T>.toObservable(): Observable<T> {
    return Observable.create({ emitter ->
        emitter.setCancellable(this::close)
        try {
            this.forEach(emitter::onNext)
            emitter.onComplete()
        } catch (e: Exception) {
            emitter.onError(e)
        }
    })
}