package com.adik993.jnapi.extensions

import com.adik993.jnapi.mime.TikaMimeDetector
import org.apache.tika.mime.MediaType
import java.io.File
import java.io.InputStream

fun File.getMediaType(): MediaType {
    return TikaMimeDetector.detect(this.toPath())
}

fun InputStream.getMediaType(): MediaType {
    return TikaMimeDetector.detect(this)
}

fun ByteArray.getMediaType(): MediaType {
    return TikaMimeDetector.detect(this)
}