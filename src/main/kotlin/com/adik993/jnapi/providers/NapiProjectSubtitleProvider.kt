package com.adik993.jnapi.providers

import com.adik993.jnapi.logging.loggerFor
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class NapiProjectSubtitleProvider : SubtitleProvider {
    val log = loggerFor<NapiProjectSubtitleProvider>()

    val ARCHIVE_PASSWORD = "iBlm8NTigvru0Jr0"

    override fun download(hash: String, lang: String): Observable<String> {
        // GET http://www.napiprojekt.pl/unit_napisy/dl.php?l=ENG&f=dc3cfb9a57309f42f308b5ca3e321e29&t=66fc5&v=other&kolejka=false&nick=&pass=&napios=Linux/UNIX
        log.info("Downloading {} subtitles for file {}", lang, hash)
        return Observable.just("dummy")
                .delay(10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
    }
}