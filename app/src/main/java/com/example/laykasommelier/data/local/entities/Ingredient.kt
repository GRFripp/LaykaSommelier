package com.example.laykasommelier.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Ingredients"
)
data class Ingredient (
    @PrimaryKey(autoGenerate = true)
    val ingredientID: Long = 0,
    @ColumnInfo("ingredientName")
    val ingredientName : String,
    @ColumnInfo("ingredientAcidity")
    val ingredientAcidity : Double = 7.0,
    @ColumnInfo("ingredientSugarLevel")
    val ingredientSugarLevel: Double = 0.0,
    @ColumnInfo("ingredientAbv")
    val ingredientAbv : Double = 0.00,
    @ColumnInfo("ingredientImageUrl")
    val ingredientImageUrl: String
)