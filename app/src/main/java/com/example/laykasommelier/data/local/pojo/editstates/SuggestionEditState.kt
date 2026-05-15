package com.example.laykasommelier.data.local.pojo.editstates

data class SuggestionEditState(
    // Данные коктейля (аналогично CocktailEditState)
    val name: String = "",
    val volume: String = "",
    val acidity: String = "",
    val sugarLevel: String = "",
    val abv: String = "",
    val glass: String = "",
    val makingMethodId: Long = -1L,
    val description: String = "",
    val author: String = "",
    val serving: String = "",
    // Специфичные для предложения
    val employeeName: String = "",   // имя сотрудника (только для чтения)
    val suggestionStatus: String = "pending", // "pending", "approved", "rejected"
    val imageUrl: String = ""
)
