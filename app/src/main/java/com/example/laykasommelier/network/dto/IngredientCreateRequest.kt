package com.example.laykasommelier.network.dto

data class IngredientCreateRequest(
    val name: String,
    val acidity: Double,
    val sugarLevel: Double,
    val abv: Double,
    val imageUrl: String
)