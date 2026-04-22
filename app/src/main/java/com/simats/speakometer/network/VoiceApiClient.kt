package com.simats.speakometer.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object VoiceApiClient {
    // Using VS Code Dev Tunnels URL for testing on a physical mobile device
    private const val BASE_URL = "http://180.235.121.245:8041/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Show headers and body text so we can debug the multipart upload
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        // AI Audio processing with Whisper on CPU can take 1-3 minutes
        .connectTimeout(300, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val analysisService: VoiceApiService by lazy {
        retrofit.create(VoiceApiService::class.java)
    }
}
