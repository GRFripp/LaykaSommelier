package com.example.laykasommelier.data.local.pojo.editstates

data class IngredientEditState(
    val name: String = "",
    val acidity: String = "",
    val sugarLevel: String = "",
    val abv: String = "",
    val imageUrl: String = "",
    val selectedDescriptorIds: Set<Long> = emptySet(),
    val searchQuery: String = "",
    val selectedCategoryId: Long? = null
)