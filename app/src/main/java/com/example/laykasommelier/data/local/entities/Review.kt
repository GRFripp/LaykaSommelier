package com.example.laykasommelier.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Reviews",
    foreignKeys = [
        ForeignKey(
            entity = Drink::class,
            parentColumns = ["drinkID"],
            childColumns = ["reviewedDrinkID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Source::class,
            parentColumns = ["sourceID"],
            childColumns = ["reviewSourceID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Review(
    @PrimaryKey(autoGenerate = true)
    val reviewID : Long = 0,
    @ColumnInfo("reviewedDrinkID")
    val reviewedDrinkID : Long = 0,
    @ColumnInfo("reviewSourceID")
    val reviewSourceID: Long = 0,
    @ColumnInfo("reviewUrl")
    val reviewUrl: String?
)
