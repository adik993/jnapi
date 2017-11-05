package com.adik993.jnapi.mime

import com.adik993.jnapi.extensions.toFullPath
import org.apache.tika.config.TikaConfig
import org.apache.tika.detect.Detector
import org.apache.tika.io.TikaInputStream
import org.apache.tika.metadata.Metadata
import org.apache.tika.mime.MediaType
import java.io.InputStream
import java.nio.file.Path

object TikaMimeDetector {
    private val detector: Detector = TikaConfig.getDefaultConfig().detector

    fun detect(path: Path): MediaType {
        val fullPath = path.toFullPath()
        return detect(TikaInputStream.get(fullPath), fullPath.fileName.toString())
    }

    fun detect(input: ByteArray): MediaType {
        return detect(TikaInputStream.get(input))
    }

    fun detect(input: InputStream): MediaType {
        return detect(TikaInputStream.get(input))
    }

    private fun detect(stream: TikaInputStream, fileName: String? = null): MediaType {
        val metadata = Metadata()
        metadata.set(Metadata.RESOURCE_NAME_KEY, fileName)
        return detector.detect(stream, metadata) ?: MediaType.parse("unknown")
    }
}