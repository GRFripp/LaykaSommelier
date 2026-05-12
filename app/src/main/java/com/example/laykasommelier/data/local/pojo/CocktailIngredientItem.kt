package com.example.laykasommelier.data.local.pojo

data class CocktailIngredientItem(
    val ingredientId: Long,
    val ingredientName: String,
    val ingredientAbv: Double,
    val ingredientAcidity: Double,
    val ingredientSugarLevel: Double,
    val volumeInCocktail: Double,
    val descriptors: List<DescriptorChip>  // DescriptorChip у вас уже есть
)