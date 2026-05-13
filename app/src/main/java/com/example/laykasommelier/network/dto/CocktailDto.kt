package com.example.laykasommelier.network.dto

data class CocktailDto(
    val id: Long = 0,
    val name: String = "",
    val volume: Double = 0.0,
    val acidity: Double = 0.0,
    val sugarLevel: Double = 0.0,
    val abv: Double = 0.0,
    val glass: String = "",
    val makingMethodId: Long = 0,   // обрати внимание: в JSON это "cocktail_making_method_id"?
    val description: String = "",
    val author: String = "unknown",
    val serving: String = "",
    val imageUrl: String = ""
)