package com.example.healthproject.ui.pharmaassistant

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GroqApi {

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer KEY_GROQ"
    )
    @POST("openai/v1/chat/completions")
    fun getCompletion(
        @Body request: GroqRequest
    ): Call<GroqResponse>
}
