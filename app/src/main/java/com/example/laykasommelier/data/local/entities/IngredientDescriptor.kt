package com.example.laykasommelier.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "IngredientsDescriptors",
    primaryKeys = ["ingredientID","descriptorID"],
    foreignKeys = [
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["ingredientID"],
            childColumns = ["ingredientID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Descriptor::class,
            parentColumns = ["descriptorID"],
            childColumns = ["descriptorID"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class IngredientDescriptor(
    val ingredientID : Long = 0,
    val descriptorID : Long = 0
)
