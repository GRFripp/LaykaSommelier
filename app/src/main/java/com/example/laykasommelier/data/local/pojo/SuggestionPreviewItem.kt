package com.example.laykasommelier.data.local.pojo

data class SuggestionPreviewItem(
    val suggestionId: Long,
    val cocktailId: Long,
    val cocktailName: String,
    val employeeName: String,
    val status: String,
    val imageUrl: String,          // если понадобится фото коктейля
    val descriptors: List<CocktailDescriptors>  // используем уже существующий класс CocktailDescriptors(name, color)
)
