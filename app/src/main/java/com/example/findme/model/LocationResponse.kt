package com.example.findme.model

data class LocationResponse(
    val query: String,      // IP adresi
    val country: String,    // Ülke
    val city: String,       // Şehir
    val lat: Double,        // Enlem
    val lon: Double         // Boylam
)