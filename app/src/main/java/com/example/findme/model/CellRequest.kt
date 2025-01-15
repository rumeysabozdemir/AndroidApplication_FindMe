package com.example.findme.model

data class CellRequest(
    val token: String,
    val radio: String,
    val mcc: Int,
    val mnc: Int,
    val cells: List<CellInfo>
)

data class CellInfo(
    val lac: Int,
    val cid: Int
)