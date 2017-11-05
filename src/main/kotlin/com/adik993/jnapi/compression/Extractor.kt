package com.adik993.jnapi.compression

import io.reactivex.Observable
import org.apache.tika.mime.MediaType
import java.io.File
import java.util.stream.Collectors

interface Extractor {
    val supportedMediaTypes: List<MediaType>

    fun extractToFile(compressed: File, destDir: File, password: String?, bufferSize: Int): Observable<File>

    fun extractInMemory(compressed: ByteArray, password: String?): Observable<ByteArray>

    fun toMap(): Map<MediaType, Extractor> {
        return this.supportedMediaTypes.stream()
                .collect(Collectors.toMap({ it }, { this }))
    }
}