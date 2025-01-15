package com.example.findme.network

import com.example.findme.model.CellLocationResponse
import com.example.findme.model.CellRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenCellIDApi {
    @POST("process.php")
    fun getCellLocation(@Body request: CellRequest): Call<CellLocationResponse>
}