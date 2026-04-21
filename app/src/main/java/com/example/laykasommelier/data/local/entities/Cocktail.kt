package com.example.laykasommelier.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Cocktails",
    foreignKeys = [
        ForeignKey(
            entity = MakingMethod::class,
            parentColumns = ["makingMethodID"],
            childColumns = ["cocktailMakingMethodID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Cocktail(
    @PrimaryKey(autoGenerate = true)
    val cocktailID : Long =0,
    @ColumnInfo("cocktailName")
    val cocktailName : String,
    @ColumnInfo("cocktailVolume")
    val cocktailVolume: Double = 0.00,
    @ColumnInfo("cocktailAcidity")
    val cocktailAcidity: Double = 0.00,
    @ColumnInfo("cocktailSugarLevel")
    val cocktailSugarLevel: Double = 0.00,
    @ColumnInfo("cocktailAbv")
    val cocktailAbv: Double = 0.00,
    @ColumnInfo("cocktailGlass")
    val cocktailGlass: String,
    @ColumnInfo("cocktailMakingMethodID")
    val cocktailMakingMethodID: Long,
    @ColumnInfo("cocktailDescription")
    val cocktailDescription: String,
    @ColumnInfo("cocktailAuthor")
    val cocktailAuthor: String = "unknown",
    @ColumnInfo("cocktailServing")
    val cocktailServing: String
)
