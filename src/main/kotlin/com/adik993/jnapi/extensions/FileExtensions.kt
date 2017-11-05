package com.adik993.jnapi.extensions

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.input.BoundedInputStream
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

fun File.md5sum(nFirstBytes: Long = -1): String {
    val input = if (nFirstBytes > 0) BoundedInputStream(this.inputStream(), nFirstBytes) else this.inputStream()
    return DigestUtils.md5Hex(input)
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

fun File.withoutExtension(): File {
    return File(FilenameUtils.removeExtension(this.path))
}

fun File.replaceExtension(extension: String): File {
    return File(withoutExtension().path + "." + extension)
}

fun File.txt(): File {
    return replaceExtension("txt")
}