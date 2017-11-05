package com.adik993.jnapi

import com.adik993.jnapi.exceptions.PermissionDeniedException
import com.adik993.jnapi.logging.loggerFor
import com.adik993.jnapi.providers.NapiProjectSubtitleProvider
import com.adik993.jnapi.providers.SubtitleOptions
import com.adik993.jnapi.providers.SubtitleProvider
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import java.io.File
import java.util.stream.Collectors

class JNapi {
    val log = loggerFor<JNapi>()

    private val providers: List<SubtitleProvider>
    private val providersMap: Map<String, SubtitleProvider>

    constructor(providers: List<SubtitleProvider> = listOf(NapiProjectSubtitleProvider())) {
        this.providers = providers
        this.providersMap = this.providers.stream().collect(Collectors.toMap({ it.name }, { it }))
    }

    fun search(file: List<File>): Observable<SubtitleOptions> {
        return Observable.merge(file.map(this::search))
    }

    fun search(file: File): Observable<SubtitleOptions> {
        return if (file.isDirectory) searchDir(file)
        else searchFile(file)
    }

    fun download(option: SubtitleOptions.Option): Observable<File> {
        return this.providersMap[option.providerName]?.download(option) ?: throw IllegalStateException("Invalid provider name")
    }

    private fun searchDir(dir: File): Observable<SubtitleOptions> {
        return dir.walkTopDown().toObservable()
                .filter(File::isFile)
                .concatMap(this::searchFile)
    }

    private fun searchFile(file: File): Observable<SubtitleOptions> {
        log.info("searching subtitles for {}", file)
        if (!file.canRead()) {
            log.warn("Permission denied for {}", file)
            return Observable.error(PermissionDeniedException("Cannot read file: $file"))
        }
        return Observable.merge(providers.map { it.search(file, "ENG") })
    }
}
