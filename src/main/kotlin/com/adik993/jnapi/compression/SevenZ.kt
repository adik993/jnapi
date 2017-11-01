package com.adik993.jnapi.compression

import com.adik993.jnapi.extensions.toFullPathFile
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

fun File.extract7z(destDir: File, password: String? = null, bufferSize: Int = 1024) {
    val absoluteDestDir = destDir.toFullPathFile()
    destDir.mkdirs()
    val archive = SevenZFile(this.toFullPathFile(), password?.toByteArray(StandardCharsets.UTF_16LE))
    archive.useForEach { entry ->
        val file = File(absoluteDestDir, entry.name)
        if (entry.isDirectory) file.mkdirs()
        else extract7zFile(file.outputStream(), archive, bufferSize)
    }
}

internal fun extract7zFile(outputStream: FileOutputStream, archive: SevenZFile, bufferSize: Int) {
    BufferedOutputStream(outputStream).use { writer ->
        val buffer = ByteArray(bufferSize)
        var read = 0
        while (read != -1) {
            writer.write(buffer, 0, read)
            read = archive.read(buffer, 0, buffer.size)
        }
    }
}

fun SevenZFile.useForEach(block: (SevenZArchiveEntry) -> Unit) {
    this.use { forEach(block) }
}

fun SevenZFile.forEach(block: (SevenZArchiveEntry) -> Unit) {
    var entry = this.nextEntry
    while (entry != null) {
        block(entry)
        entry = this.nextEntry
    }
}