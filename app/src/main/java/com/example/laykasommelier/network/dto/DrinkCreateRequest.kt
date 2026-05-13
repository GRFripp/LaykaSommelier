package com.example.laykasommelier.network.dto

data class DrinkCreateRequest(
    val name: String,
    val type: String,
    val subType: String?,
    val country: String?,
    val producer: String?,
    val aged: Int,
    val abv: Double,
    val imageUrl: String
)
