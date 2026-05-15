package com.example.laykasommelier.data.local.pojo.editstates

data class CocktailEditState(
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
    val imageUrl: String = ""
)