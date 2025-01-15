package com.example.findme.model

data class PhoneNumberResponse(
    val valid: Boolean?,
    val country_name: String?,
    val location: String?
)