package com.adik993.jnapi

import com.adik993.jnapi.extensions.md5sum
import com.adik993.jnapi.logging.loggerFor
import com.adik993.jnapi.providers.SubtitleProvider
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import java.io.File

class JNapi(val providers: List<SubtitleProvider> = emptyList()) {
    val log = loggerFor<JNapi>()

    fun download(file: List<File>): Observable<JNapiResult> {
        return Observable.merge(file.map(this::download))
    }

    fun download(file: File): Observable<JNapiResult> {
        return if (file.isDirectory) downloadDir(file)
        else downloadFile(file)
    }

    private fun downloadDir(dir: File): Observable<JNapiResult> {
        return dir.walkTopDown().toObservable()
                .filter(File::isFile)
                .concatMap(this::downloadFile)
    }

    private fun downloadFile(file: File): Observable<JNapiResult> {
        log.info("Downloading subtitles for {}", file)
        if (!file.canRead()) {
            log.warn("Permission denied for {}", file)
            return Observable.just(JNapiResult(file, false))
        }
        val md5 = file.md5sum()
        return Observable.just(JNapiResult(file, true))
    }
}
