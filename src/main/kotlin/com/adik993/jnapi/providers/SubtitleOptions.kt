package com.adik993.jnapi.providers

import java.io.File

class SubtitleOptions(val source: File, val options: List<Option>) {

    fun isActionRequired(): Boolean {
        return options.size > 1
    }

    fun isSubtitlesNotFound(): Boolean {
        return options.isEmpty()
    }

    fun onlyOption(): Option {
        if (isActionRequired()) throw IllegalStateException("Action is required use download of one of the options")
        else if (isSubtitlesNotFound()) throw IllegalStateException("No subtitles found handle it")
        return options[0]
    }

    data class Option(val providerName: String, val source: File, val id: String, val lang: String)

    companion object {
        fun noSubtitles(source: File): SubtitleOptions {
            return SubtitleOptions(source, emptyList())
        }
    }
}