package com.adik993.jnapi.compression

import com.adik993.jnapi.extensions.toFullPathFile
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.Spliterator.*
import java.util.function.Consumer
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.streams.toList

fun File.extract7z(destDir: File, password: String? = null, bufferSize: Int = 1024): List<File> {
    val absoluteDestDir = destDir.toFullPathFile()
    val archive = SevenZFile(this.toFullPathFile(), password?.toByteArray(StandardCharsets.UTF_16LE))
    archive.use {
        return archive.stream()
                .map { it.createFile(absoluteDestDir) }
                .filter { !it.isDirectory }
                .peek { extract7zFile(it.outputStream(), archive, bufferSize) }
                .toList()
    }
}

fun SevenZArchiveEntry.createFile(root: File): File {
    val file = File(root, this.name)
    if (isDirectory) file.mkdirs()
    else file.parentFile.mkdirs() && file.createNewFile()
    return file
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

fun SevenZFile.stream(): Stream<SevenZArchiveEntry> {
    return StreamSupport.stream(SevenZFileSpliterator(this), false).onClose(this::close)
}

class SevenZFileSpliterator(private val archive: SevenZFile) : Spliterator<SevenZArchiveEntry> {

    override fun tryAdvance(action: Consumer<in SevenZArchiveEntry>?): Boolean {
        val entry = archive.nextEntry
        val hasValue = entry != null
        if (hasValue) action?.accept(entry)
        return hasValue
    }

    override fun trySplit(): Spliterator<SevenZArchiveEntry>? {
        return null
    }

    override fun estimateSize(): Long {
        return archive.entries.count().toLong()
    }

    override fun characteristics(): Int {
        return ORDERED or IMMUTABLE or NONNULL or SIZED
    }
}