package com.adik993.jnapi.extensions

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets.UTF_16LE

fun File.md5sum(): String {
    return DigestUtils.md5Hex(this.inputStream())
}

fun File.toFullPathFile(): File {
    val path = if (this.path.startsWith('~')) this.path.replaceFirst("~", getHomeDir()) else this.path
    return File(path).absoluteFile
}

private fun getHomeDir() = System.getProperty("user.home")