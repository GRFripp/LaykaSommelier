package com.example.laykasommelier.data.local.pojo

data class DrinkListRow(
    val drinkId: Long,
    val drinkName: String,
    val imageUrl: String,
    val desCategoryName: String?,
    val desCategoryColor: String?,
    val desCategoryCount: Int
)
