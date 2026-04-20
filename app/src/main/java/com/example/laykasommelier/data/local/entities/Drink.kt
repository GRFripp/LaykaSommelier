package com.example.laykasommelier.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.w3c.dom.Text

@Entity(
    tableName="Drinks"
)
data class Drink(
    @PrimaryKey(autoGenerate = true)
    val drinkId: Int,
    @ColumnInfo(name = "drinkName")
    val drinkName: String,
    @ColumnInfo(name = "drinkType")
    val drinkType: String,
    @ColumnInfo(name = "drinkSubType")
    val drinkSubType : String?,
    @ColumnInfo(name = "drinkCountry")
    val drinkCountry: String?,
    @ColumnInfo(name = "drinkProducer")
    val drinkProducer: String?,
    @ColumnInfo(name = "drinkAged")
    val drinkAged: Int = 0,
    @ColumnInfo(name ="drinkAbv")
    val drinkAbv: Double = 0.00
    )
