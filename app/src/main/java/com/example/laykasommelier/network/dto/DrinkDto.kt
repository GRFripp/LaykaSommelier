package com.example.laykasommelier.network.dto


data class DrinkDto(
    val id: Long = 0,
    val name: String = "",
    val type: String = "",
    val subType: String? = null,
    val country: String? = null,
    val producer: String? = null,
    val aged: Int = 0,
    val abv: Double = 0.0,
    val imageUrl: String = ""
)
