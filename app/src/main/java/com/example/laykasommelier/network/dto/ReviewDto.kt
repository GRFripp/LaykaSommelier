package com.example.laykasommelier.network.dto

data class ReviewDto(
    val id: Long = 0,
    val reviewedDrinkId: Long = 0,
    val sourceId: Long = 0,
    val url: String? = null
)