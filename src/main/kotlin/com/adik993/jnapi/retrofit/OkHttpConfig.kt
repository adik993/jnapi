package com.adik993.jnapi.retrofit

import com.google.common.io.Files
import okhttp3.Cache
import okhttp3.OkHttpClient


private val cacheSize: Long = 10 * 1024 * 1024
private val cache = Cache(Files.createTempDir(), cacheSize)

val okHttpClient = OkHttpClient.Builder()
        .cache(cache)
        .build()