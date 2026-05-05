package com.example.laykasommelier.data.local.pojo

data class DrinkListPreviews(
    val drinkId: Long,
    val drinkName: String,
    val imageUrl: String,
    val categories: List<CategoryColor>
)
data class CategoryColor(
    val name: String,
    val color: String?,
    val count: Int
)