package com.adik993.jnapi

import com.adik993.jnapi.exceptions.PermissionDeniedException
import com.adik993.jnapi.extensions.md5sum
import com.adik993.jnapi.logging.loggerFor
import com.adik993.jnapi.providers.NapiProjectSubtitleProvider
import com.adik993.jnapi.providers.SubtitleOptions
import com.adik993.jnapi.providers.SubtitleProvider
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import java.io.File

class JNapi(val providers: List<SubtitleProvider> = listOf(NapiProjectSubtitleProvider())) {
    val log = loggerFor<JNapi>()

    fun download(file: List<File>): Observable<SubtitleOptions> {
        return Observable.merge(file.map(this::download))
    }

    fun download(file: File): Observable<SubtitleOptions> {
        return if (file.isDirectory) downloadDir(file)
        else downloadFile(file)
    }

    private fun downloadDir(dir: File): Observable<SubtitleOptions> {
        return dir.walkTopDown().toObservable()
                .filter(File::isFile)
                .concatMap(this::downloadFile)
    }

    private fun downloadFile(file: File): Observable<SubtitleOptions> {
        log.info("Downloading subtitles for {}", file)
        if (!file.canRead()) {
            log.warn("Permission denied for {}", file)
            return Observable.error(PermissionDeniedException("Cannot read file: $file"))
        }
        return Observable.merge(providers.map { it.download(file, "ENG") })
    }
}
