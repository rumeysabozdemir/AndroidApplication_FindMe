package com.example.findme.model

data class CellLocationResponse(
    val lat: Double,
    val lon: Double,
    val range: Int?,
    val address: String?
)