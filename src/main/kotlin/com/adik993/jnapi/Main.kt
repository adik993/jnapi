package com.adik993.jnapi

import com.adik993.jnapi.extensions.toFullPathFile
import com.adik993.jnapi.logging.loggerFor
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import io.reactivex.Observable
import java.io.File
import javax.ws.rs.NotSupportedException
import kotlin.system.exitProcess

class Main

val log = loggerFor<Main>()

fun main(args: Array<String>) {
    val parser = ArgParser(args)
    val filenames by parser.positionalList("SOURCE",
            help = "list of filenames") { File(this).toFullPathFile() }.default(emptyList())
    if (filenames.isEmpty()) {
        println("Nothing to do")
        exitProcess(1)
    }
    val jnapi = JNapi()
    jnapi.search(filenames)
            .flatMap { options ->
                if (options.isActionRequired()) {
                    Observable.error(NotSupportedException("Choosing not supported yet"))
                } else if (options.isSubtitlesNotFound()) {
                    log.info("Subtitles not found for file {}", options.source)
                    Observable.empty()
                } else {
                    jnapi.download(options.onlyOption())
                }
            }.subscribe({ log.info("Subtitles saved to {}", it) })
}