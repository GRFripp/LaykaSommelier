package com.example.laykasommelier.network.dto

data class VerifyTokenResponse(
    val uid: String,
    val email: String,
    val role: String,
    val employeeId: Long
)
