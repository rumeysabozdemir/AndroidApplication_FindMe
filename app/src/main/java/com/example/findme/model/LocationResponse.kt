package com.example.findme.model

data class LocationResponse(
    val status: String,
    val country: String,
    val countryCode: String,
    val region: String,
    val regionName: String,
    val city: String,
    val zip: String?,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val isp: String,
    val org: String?,
    val `as`: String?,
    val query: String
)