package com.example.laykasommelier.network.dto

data class IngredientUpdateRequest(
    val name: String,
    val acidity: Double,
    val sugarLevel: Double,
    val abv: Double,
    val imageUrl: String
)