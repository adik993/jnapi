package com.adik993.jnapi.providers

import com.adik993.jnapi.logging.loggerFor
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class NapiProjectSubtitleProvider : SubtitleProvider {
    val log = loggerFor<NapiProjectSubtitleProvider>()

    override fun download(hash: String, lang: String): Observable<String> {
        log.info("Downloading {} subtitles for file {}", lang, hash)
        return Observable.just("dummy")
                .delay(10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
    }
}