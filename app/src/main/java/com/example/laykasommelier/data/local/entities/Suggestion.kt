package com.example.laykasommelier.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    "Suggestions",
    foreignKeys = [
        ForeignKey(
            Employee::class,
            ["employeeID"],
            ["suggestionEmployeeID"],
            ForeignKey.CASCADE
        ),
        ForeignKey(
            Cocktail::class,
            ["cocktailID"],
            ["suggestedCocktailID"],
            ForeignKey.CASCADE
        )
    ]
)
data class Suggestion(
    @PrimaryKey(true)
    val suggestionID : Long = 0,
    @ColumnInfo("suggestedCocktailID")
    val suggestedCocktailID: Long = 0,
    @ColumnInfo("suggestionEmployeeID")
    val suggestionEmployeeID: Long = 0,
    @ColumnInfo("suggestionStatus")
    val suggestionStatus: String
)
