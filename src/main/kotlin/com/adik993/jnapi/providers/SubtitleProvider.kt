package com.adik993.jnapi.providers

import io.reactivex.Observable
import java.io.File

interface SubtitleProvider {
    val name: String
    fun search(file: File, lang: String): Observable<SubtitleOptions>
    fun download(option: SubtitleOptions.Option): Observable<File>
}