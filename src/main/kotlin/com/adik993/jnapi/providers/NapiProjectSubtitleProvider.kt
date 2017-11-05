package com.adik993.jnapi.providers

import com.adik993.jnapi.extensions.extract
import com.adik993.jnapi.extensions.md5sum
import com.adik993.jnapi.extensions.txt
import com.adik993.jnapi.logging.loggerFor
import io.reactivex.Observable
import org.apache.commons.io.IOUtils
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.convert.Convert
import org.simpleframework.xml.convert.Converter
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.InputNode
import org.simpleframework.xml.stream.OutputNode
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.File
import java.util.*

class NapiProjectSubtitleProvider : SubtitleProvider {
    override val name: String = "NapiProjekt"
    private val log = loggerFor<NapiProjectSubtitleProvider>()

    companion object {
        private val ARCHIVE_PASSWORD = "iBlm8NTigvru0Jr0"
        private val NUMBER_OF_BYTES_MD5: Long = 10 * Math.pow(2.0, 20.0).toLong()
    }

    val api = NapiProjekt.create()

    override fun download(file: File, lang: String): Observable<SubtitleOptions> {
        val hash = file.md5sum(NUMBER_OF_BYTES_MD5)
        log.info("Downloading {} subtitles for file {} with hash {}", lang, file, hash)
        return api.fetchSubtitles(hash, lang, SubtitleTxtMode.SevenZ.value)
                .filter { it.body() != null }
                .map { it.body()!! }
                .flatMap { response ->
                    response.subtitles.content.bytes.extract(ARCHIVE_PASSWORD)
                            .doOnNext { response.subtitles.content = ByteContent(it) }
                            .map { response }
                }
                .doOnNext { log.debug("Subtitles successfully fetched for file {}", file) }
                .map { SubtitleOptions(file, SubtitleOptions.Option(name, it.subtitles.toDownloadObservable(file.txt()))) }
                .onErrorResumeNext { throwable: Throwable ->
                    log.warn("Error downloading subtitles for file {}", file, throwable)
                    Observable.just(SubtitleOptions.noSubtitles(file))
                }
    }

    private fun Subtitles.toDownloadObservable(output: File): Observable<File> {
        return Observable.fromCallable {
            val bytes = content.bytes
            log.debug("Saving subtitles of size {} to {}", bytes.size, output)
            IOUtils.copy(bytes.inputStream(), output.outputStream())
            log.debug("Subtitles saved of size {} saved to {}", bytes.size, output)
            output
        }
    }
}


interface NapiProjekt {
    @FormUrlEncoded
    @POST("api-napiprojekt3.php")
    fun fetchSubtitles(@Field("downloaded_subtitles_id") md5sum: String,
                       @Field("downloaded_subtitles_lang") lang: String,
                       @Field("downloaded_subtitles_txt") subtitleTxt: Int = SubtitleTxtMode.Text.value,
                       @Field("mode") mode: Int = 1,
                       @Field("client") client: String = "Napiprojekt",
                       @Field("client_ver") clientVersion: String = "2.2.0.2399"): Observable<Response<NapiProjektResponse>>

    companion object {
        fun create(): NapiProjekt {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(SimpleXmlConverterFactory.create(Persister(AnnotationStrategy())))
                    .baseUrl("http://napiprojekt.pl/api/")
                    .build()
            return retrofit.create(NapiProjekt::class.java)
        }
    }
}

enum class SubtitleTxtMode(val value: Int) {
    Text(1), SevenZ(17)
}

@Root(name = "result", strict = false)
data class NapiProjektResponse(
        @field:Element(name = "status") var status: String,
        @field:Element var subtitles: Subtitles) {
    constructor() : this("", Subtitles())
}

@Root(name = "subtitles")
data class Subtitles(
        @field:Element(name = "id") var id: String,
        @field:Element(name = "subs_hash") var hash: String,
        @field:Element(name = "filesize") var fileSize: Long,
        @field:Element(name = "author") var author: String,
        @field:Element(name = "uploader") var uploader: String,
        @field:Element(name = "upload_date") var uploadDate: Date,
        @field:Element(name = "content") @field:Convert(Base64Converter::class) var content: ByteContent) {
    constructor() : this("", "", 0L, "", "", Date(), ByteContent(ByteArray(0)))
}

data class ByteContent(val bytes: ByteArray)

class Base64Converter : Converter<ByteContent> {
    override fun write(node: OutputNode?, value: ByteContent?) {
        node?.value = Base64.getEncoder().encodeToString(value?.bytes)
    }

    override fun read(node: InputNode?): ByteContent {
        return ByteContent(Base64.getDecoder().decode(node?.value))
    }
}