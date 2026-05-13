package com.example.laykasommelier.network.dto

data class CocktailCreateRequest(
    val name: String,
    val volume: Double,
    val acidity: Double,
    val sugarLevel: Double,
    val abv: Double,
    val glass: String,
    val makingMethodId: Long,
    val description: String,
    val author: String,
    val serving: String,
    val imageUrl: String
)
