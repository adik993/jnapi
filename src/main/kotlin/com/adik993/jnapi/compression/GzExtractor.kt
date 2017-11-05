package com.adik993.jnapi.compression

import com.adik993.jnapi.extensions.toFullPathFile
import com.adik993.jnapi.extensions.withoutExtension
import io.reactivex.Observable
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.io.IOUtils
import org.apache.tika.mime.MediaType
import java.io.File

class GzExtractor : Extractor {
    override val supportedMediaTypes = listOf(MediaType.parse("application/x-gzip"),
            MediaType.parse("application/gzip"))

    override fun extract(compressed: File, destDir: File, password: String?, bufferSize: Int): Observable<File> {
        return Observable.fromCallable({ extractSync(compressed, destDir, bufferSize) })
    }

    private fun extractSync(compressed: File, destDir: File, bufferSize: Int): File {
        val input = GzipCompressorInputStream(compressed.toFullPathFile().inputStream())
        val outFile = File(destDir.toFullPathFile(), prepareOutFileName(compressed))
        val output = outFile.outputStream()
        input.use {
            output.use {
                IOUtils.copy(input, output, bufferSize)
            }
        }
        return outFile
    }

    private fun prepareOutFileName(compressed: File) = compressed.withoutExtension().name
}