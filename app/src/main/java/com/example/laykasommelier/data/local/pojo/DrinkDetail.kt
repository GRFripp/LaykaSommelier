package com.example.laykasommelier.data.local.pojo

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class DrinkDetail(

    val name: String,

    val type: String,

    val subType : String?,

    val country: String?,

    val producer: String?,

    val aged: Int,

    val abv: Double,
    val imageUrl: String


)
