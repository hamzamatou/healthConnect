package com.example.healthproject.ui.pharmaassistant

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GroqApi {

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer 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"
    )
    @POST("openai/v1/chat/completions")
    fun getCompletion(
        @Body request: GroqRequest
    ): Call<GroqResponse>
}
