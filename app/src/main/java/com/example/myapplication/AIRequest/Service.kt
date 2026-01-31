package com.example.myapplication.AIRequest

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface HuggingFaceApi {
    @Headers(
        "Authorization: Bearer -",
        "Content-Type: application/json"
    )
    @POST("models/openai-community/gpt2")
    fun queryModel(@Body inputs: Map<String, String>): Call<Map<String, String>>
}
