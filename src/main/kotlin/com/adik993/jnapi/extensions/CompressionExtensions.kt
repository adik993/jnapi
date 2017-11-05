package com.adik993.jnapi.extensions

import com.adik993.jnapi.compression.Extractors
import io.reactivex.Observable
import java.io.File

fun File.extract(destDir: File, password: String? = null, bufferSize: Int = 8192): Observable<File> {
    return Extractors.forMediaType(this.getMediaType()).extractToFile(this, destDir, password, bufferSize)
}

fun ByteArray.extract(password: String?): Observable<ByteArray> {
    return Extractors.forMediaType(this.getMediaType()).extractInMemory(this, password)
}