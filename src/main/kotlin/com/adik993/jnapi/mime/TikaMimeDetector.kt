package com.adik993.jnapi.mime

import com.adik993.jnapi.extensions.toFullPath
import org.apache.tika.config.TikaConfig
import org.apache.tika.detect.Detector
import org.apache.tika.io.TikaInputStream
import org.apache.tika.metadata.Metadata
import org.apache.tika.mime.MediaType
import java.nio.file.Path

object TikaMimeDetector {
    private val detector: Detector = TikaConfig.getDefaultConfig().detector

    fun detect(path: Path): MediaType {
        val fullPath = path.toFullPath()
        val stream = TikaInputStream.get(fullPath)
        val metadata = Metadata()
        metadata.set(Metadata.RESOURCE_NAME_KEY, fullPath.fileName.toString())
        return detector.detect(stream, metadata) ?: MediaType.parse("unknown")
    }
}