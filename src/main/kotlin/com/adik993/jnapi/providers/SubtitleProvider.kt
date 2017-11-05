package com.adik993.jnapi.providers

import io.reactivex.Observable
import java.io.File

interface SubtitleProvider {
    val name: String
    fun download(file: File, lang: String): Observable<SubtitleOptions>
}