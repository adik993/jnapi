package com.adik993.jnapi.compression

import com.adik993.jnapi.extensions.toFullPathFile
import com.adik993.jnapi.extensions.toObservable
import io.reactivex.Observable
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel
import org.apache.tika.mime.MediaType
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.Spliterator.*
import java.util.function.Consumer
import java.util.stream.Stream
import java.util.stream.StreamSupport

class SevenZExtractor : Extractor {
    override val supportedMediaTypes = listOf(MediaType.parse("application/x-7z-compressed"))

    override fun extractToFile(compressed: File, destDir: File, password: String?, bufferSize: Int): Observable<File> {
        val absoluteDestDir = destDir.toFullPathFile()
        val archive = SevenZFile(compressed.toFullPathFile(), password?.toCharArray())
        return archive.stream().toObservable()
                .map { it.createFile(absoluteDestDir) }
                .filter { !it.isDirectory }
                .doOnNext { extract7zEntry(it.outputStream(), archive, bufferSize) }
    }

    override fun extractInMemory(compressed: ByteArray, password: String?): Observable<ByteArray> {
        val input = SeekableInMemoryByteChannel(compressed)
        val archive = SevenZFile(input, password?.toCharArray())
        return archive.stream().toObservable()
                .filter { !it.isDirectory }
                .map {
                    val output = ByteArrayOutputStream(it.size.toInt())
                    extract7zEntry(output, archive, it.size.toInt())
                    output.toByteArray()
                }
    }

    private fun extract7zEntry(outputStream: OutputStream, archive: SevenZFile, bufferSize: Int) {
        outputStream.use { writer ->
            val buffer = ByteArray(bufferSize)
            var read = 0
            while (read != -1) {
                writer.write(buffer, 0, read)
                read = archive.read(buffer, 0, buffer.size)
            }
        }
    }
}

private fun SevenZArchiveEntry.createFile(root: File): File {
    val file = File(root, this.name)
    if (isDirectory) file.mkdirs()
    else file.parentFile.mkdirs() && file.createNewFile()
    return file
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