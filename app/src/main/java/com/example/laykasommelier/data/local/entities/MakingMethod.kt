package com.example.laykasommelier.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "MakingMethods"
)
data class MakingMethod(
    @PrimaryKey(autoGenerate = true)
    val makingMethodID:Long=0,
    @ColumnInfo("makingMethodName")
    val makingMethodName:String,
    @ColumnInfo("makingMethodDilution")
    val makingMethodDilution:Double = 0.00
)
