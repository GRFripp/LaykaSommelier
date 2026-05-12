package com.example.laykasommelier.data.local.pojo

data class IngredientRow(
    val ingredientId: Long,
    val ingredientName: String,
    val ingredientAbv: Double,
    val ingredientAcidity: Double,
    val ingredientSugarLevel: Double,
    val volumeInCocktail: Double,
    val descriptorName: String?,
    val descriptorColor: String?
)
