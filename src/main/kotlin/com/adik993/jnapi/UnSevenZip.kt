package com.adik993.jnapi

import com.adik993.jnapi.compression.extract7z
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.ShowHelpException
import com.xenomachina.argparser.SystemExitException
import com.xenomachina.argparser.default
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val parser = ArgParser(args)
    val password by parser.storing("-p", "--password", help = "password for the archive").default(null)
    val file by parser.positional("SOURCE", help = "7z archive file path") { File(this) }
    val destDir by parser.storing("-o", help = "output directory") { File(this) }.default(null)
    try {
        parser.force()
    } catch (e: ShowHelpException) {
        val writer = System.out.writer()
        e.printUserMessage(writer, "un7z", 0)
        writer.flush()
        exitProcess(0)
    } catch (e: SystemExitException) {
        println(e.message)
        exitProcess(1)
    }
    val dest = destDir ?: file.parentFile
    println("Extracting files to $dest folder...")
    val extractedFiles = file.extract7z(dest, password, 1024 * 10)
            .doOnError({ println("Error extracting: $it") })
            .doOnNext({ println("${it.path} (${it.length()} bytes)") })
            .toList().blockingGet()
    println("Extracted ${extractedFiles.size} files")
}