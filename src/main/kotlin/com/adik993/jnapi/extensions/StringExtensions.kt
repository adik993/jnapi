package com.adik993.jnapi.extensions

import org.slf4j.helpers.MessageFormatter

fun String.format(vararg args: Any?):String {
    return MessageFormatter.arrayFormat(this, args).message
}