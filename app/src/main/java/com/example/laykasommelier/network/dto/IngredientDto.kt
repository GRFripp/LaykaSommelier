package com.example.laykasommelier.network.dto

data class IngredientDto(
    val id: Long = 0,
    val name: String = "",
    val acidity: Double = 7.0,
    val sugarLevel: Double = 0.0,
    val abv: Double = 0.0,
    val imageUrl: String = ""
)