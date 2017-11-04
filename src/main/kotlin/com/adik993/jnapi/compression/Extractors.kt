package com.adik993.jnapi.compression

import org.apache.tika.mime.MediaType
import java.util.stream.Collectors

object Extractors {
    private val extractors: Map<MediaType, Extractor>
        get() {
            return listOfNotNull(SevenZExtractor(), GzExtractor()).stream()
                    .map(Extractor::toMap)
                    .flatMap { it.entries.stream() }
                    .collect(Collectors.toMap({ it.key }, { it.value }))
        }

    fun forMediaType(mediaType: MediaType): Extractor {
        return extractors[mediaType] ?: throw MediaTypeNotSupportedException("Could not find extractor for type $mediaType")
    }
}

