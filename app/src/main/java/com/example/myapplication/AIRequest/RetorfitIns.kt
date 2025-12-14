package com.example.myapplication.AIRequest

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.net.ProxySelector
import java.util.concurrent.TimeUnit

object RetrofitIns {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .proxySelector(ProxySelector.getDefault())
        .build()

    val instance: HuggingFaceApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://router.huggingface.co/hf-inference/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(HuggingFaceApi::class.java)
    }
}