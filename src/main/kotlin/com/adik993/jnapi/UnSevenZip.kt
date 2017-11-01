package com.adik993.jnapi

import com.adik993.jnapi.compression.extract7z
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import java.io.File

fun main(args: Array<String>) {
    val parser = ArgParser(args)
    val password by parser.storing("-p", "--password", help = "password for the archive").default(null)
    val file by parser.positional("SOURCE", help = "7z archive file path") { File(this) }
    file.extract7z(file.parentFile, password)

}