package com.adik993.jnapi.providers

import io.reactivex.Single
import java.io.File

abstract class Option(val providerName: String, val source: File, val id: String, val lang: String) {
    abstract fun download(): Single<File>
}