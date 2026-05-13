package com.example.laykasommelier.network.dto

data class CocktailIngredientLinkRequest(
    val cocktailId: Long,
    val ingredientId: Long,
    val volume: Double
)