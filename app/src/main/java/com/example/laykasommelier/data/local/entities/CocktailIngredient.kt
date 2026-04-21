package com.example.laykasommelier.data.local.entities

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
    val cocktailID: Long = 0,
    val ingredientID: Long = 0
)
