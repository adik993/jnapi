package com.adik993.jnapi.providers

import io.reactivex.Observable

interface SubtitleProvider {
    fun download(hash: String, lang: String): Observable<String>
}