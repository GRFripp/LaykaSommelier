package com.example.laykasommelier.data.local.pojo

data class CocktailListPreviews(
    val cocktailId: Long,
    val cocktailName: String,
    val imageUrl: String?,
    val descriptors: List<CocktailDescriptors>
)
data class CocktailDescriptors(
    val name: String,
    val color: String
)


