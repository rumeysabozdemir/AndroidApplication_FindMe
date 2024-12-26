package com.example.findme.network

import retrofit2.Call
import retrofit2.http.GET
import com.example.findme.model.LocationResponse

interface LocationApi {
    @GET("json/")
    fun getLocation(): Call<LocationResponse>
}