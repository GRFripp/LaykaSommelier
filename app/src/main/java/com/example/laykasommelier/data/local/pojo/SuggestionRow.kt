package com.example.laykasommelier.data.local.pojo

data class SuggestionRow(
    val suggestionId: Long,
    val cocktailId: Long,
    val cocktailName: String,
    val cocktailImageUrl: String,
    val employeeName: String,
    val status: String,
    val descriptorName: String?,
    val descriptorColor: String?
)
