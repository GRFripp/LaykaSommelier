package com.example.laykasommelier.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity("CocktailsIngredients",
    primaryKeys = ["cocktailID","ingredientID"],
    foreignKeys = [
        ForeignKey(
            Cocktail::class,
            ["cocktailID"],
            ["cocktailID"],
            ForeignKey.CASCADE
        ),
        ForeignKey(
            Ingredient::class,
            ["ingredientID"],
            ["ingredientID"],
            ForeignKey.CASCADE
        )
    ])
data class CocktailIngredient(
    @ColumnInfo("cocktailID")
    val cocktailID: Long = 0,
    @ColumnInfo("ingredientID")
    val ingredientID: Long = 0,
    @ColumnInfo("ingredientVolume")
    val ingredientVolume: Double = 0.00
)
