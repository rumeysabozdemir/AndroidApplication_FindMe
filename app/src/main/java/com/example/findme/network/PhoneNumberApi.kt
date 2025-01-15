package com.example.findme.network

import com.example.findme.model.PhoneNumberResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PhoneNumberApi {
    @GET("validate")
    fun validatePhoneNumber(
        @Header("apikey") apiKey: String,
        @Query("number") phoneNumber: String
    ): Call<PhoneNumberResponse>
}