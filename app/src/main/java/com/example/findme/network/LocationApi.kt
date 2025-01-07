package com.example.findme.network

import com.example.findme.model.CellLocationResponse
import com.example.findme.model.LocationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LocationApi {
    // IP tabanlı konum alma (ör. ip-api.com için)
    @GET("json/")
    fun getLocation(): Call<LocationResponse>

    // Cell-ID tabanlı konum alma (ör. OpenCellID API için)
    @POST("process/")
    fun getCellLocation(@Body request: Any): Call<CellLocationResponse>
}