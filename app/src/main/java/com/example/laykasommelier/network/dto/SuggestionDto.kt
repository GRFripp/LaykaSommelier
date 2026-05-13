package com.example.laykasommelier.network.dto

data class SuggestionDto(
    val id: Long = 0,
    val cocktailId: Long = 0,
    val employeeId: Long = 0,
    val status: String = ""
)