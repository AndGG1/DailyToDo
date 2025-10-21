package com.example.myapplication.MainLogic.Data

import AiRequest
import AiResponseItem
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit
import retrofit2.http.Path

interface API_Service {

    @POST("models/{model}")
    suspend fun postReq(
        @Path("model") model: String,
        @Body requestBody: AiRequest,
        @Header("Authorization") token: String
    ): List<AiResponseItem>
}

//https://medium.com/@KaushalVasava/retrofit-in-android-5a28c8e988ce - Further Documentation
//https://www.youtube.com/watch?v=lmRzRKIsn1g - Further Coroutines & Threading documentation for getRes()
//https://copilot.microsoft.com/shares/6SvPs36sGhqiiBptJaDib - Important AI discussion


object RetrofitIns {
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api-inference.huggingface.co/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    val service: API_Service = retrofit.create(API_Service::class.java)
}
