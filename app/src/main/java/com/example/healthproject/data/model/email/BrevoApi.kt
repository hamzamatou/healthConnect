package com.example.healthproject.data.model.email


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface BrevoApi {
    @POST("v3/smtp/email")
    fun sendEmail(@Body body: EmailBody, @Header("api-key") apiKey: String): Call<ResponseBody>
}