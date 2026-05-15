package com.example.laykasommelier.data.local.pojo

data class CocktailListRow (
    val cId: Long,
    val cName: String,
    val dName: String?,
    val dColor: String?,
    val cImageUrl: String = ""
)