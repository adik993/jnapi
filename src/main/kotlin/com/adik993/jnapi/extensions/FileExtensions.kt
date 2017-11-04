package com.adik993.jnapi.extensions

import com.adik993.jnapi.compression.Extractors
import com.adik993.jnapi.mime.TikaMimeDetector
import io.reactivex.Observable
import org.apache.commons.codec.digest.DigestUtils
import org.apache.tika.mime.MediaType
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

fun File.md5sum(): String {
    return DigestUtils.md5Hex(this.inputStream())
}

fun File.toFullPathFile(): File {
    return File(getFullPath(this.path)).absoluteFile
}

private fun getFullPath(path: String): String? {
    return if (path.startsWith('~')) path.replaceFirst("~", getHomeDir()) else path
}

private fun getHomeDir() = System.getProperty("user.home")

fun Path.toFullPath(): Path {
    return Paths.get(getFullPath(this.toString()))
}

fun File.extract(destDir: File, password: String? = null, bufferSize: Int = 8192): Observable<File> {
    return Extractors.forMediaType(this.getMediaType()).extract(this, destDir, password, bufferSize)
}

fun File.getMediaType(): MediaType {
    return TikaMimeDetector.detect(this.toPath())
}