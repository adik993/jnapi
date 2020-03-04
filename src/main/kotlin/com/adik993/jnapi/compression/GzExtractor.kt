package com.adik993.jnapi.compression

import com.adik993.jnapi.extensions.toFullPathFile
import com.adik993.jnapi.extensions.withoutExtension
import io.reactivex.Observable
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.io.IOUtils
import org.apache.tika.mime.MediaType
import java.io.*

class GzExtractor : Extractor {
    override val supportedMediaTypes = listOf(MediaType.parse("application/x-gzip"),
            MediaType.parse("application/gzip"))

    override fun extractToFile(compressed: File, destDir: File, password: String?, bufferSize: Int): Observable<File> {
        val input = compressed.toFullPathFile().inputStream()
        val outFile = File(destDir.toFullPathFile(), prepareOutFileName(compressed))
        val output = outFile.outputStream()
        return Observable.fromCallable {
            extractSync(input, output, bufferSize)
            outFile
        }
    }

    override fun extractInMemory(compressed: ByteArray, password: String?): Observable<ByteArray> {
        return Observable.fromCallable {
            val output = ByteArrayOutputStream()
            extractSync(ByteArrayInputStream(compressed), output, compressed.size)
            output.toByteArray()
        }
    }

    private fun extractSync(inputStream: InputStream, output: OutputStream, bufferSize: Int) {
        val input = GzipCompressorInputStream(inputStream)
        input.use { _ ->
            output.use {
                IOUtils.copy(input, output, bufferSize)
            }
        }
    }

    private fun prepareOutFileName(compressed: File) = compressed.withoutExtension().name
}