package com.adik993.jnapi.providers

import io.reactivex.Observable
import java.io.File

class SubtitleOptions(val source: File, vararg val options: Option) {

    fun isActionRequired(): Boolean {
        return options.size > 1
    }

    fun isSubtitlesNotFound(): Boolean {
        return options.isEmpty()
    }

    fun download(): Observable<File> {
        if (isActionRequired()) throw IllegalStateException("Action is required use download of one of the options")
        else if (isSubtitlesNotFound()) throw IllegalStateException("No subtitles found handle it")
        return options[0].download()
    }

    class Option(val providerName: String, val downloadObservable: Observable<File>) {
        fun download(): Observable<File> {
            return this.downloadObservable
        }
    }

    companion object {
        fun noSubtitles(source: File): SubtitleOptions {
            return SubtitleOptions(source)
        }
    }
}