package com.example.laykasommelier.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "DescriptorCategories"
)
data class DescriptorCategory(
    @PrimaryKey(autoGenerate = true)
    val descriptorCategoryID : Int,
    @ColumnInfo("descriptorCategoryName")
    val descriptorCategoryName: String,
    @ColumnInfo("descriptorCategoryColor")
    val descriptorCategoryColor: String
)
