package com.example.laykasommelier.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.util.TableInfo

@Entity(
    tableName = "Descriptors",
    foreignKeys = [
        ForeignKey(
            entity = DescriptorCategory::class,
            parentColumns = ["descriptorCategoryID"],
            childColumns = ["descriptorCategory"],
            ForeignKey.CASCADE
        )
    ]
)
data class Descriptor(
    @PrimaryKey(autoGenerate = true)
    val descriptorID: Long = 0,
    @ColumnInfo ("descriptorName")
    val descriptorName: String,
    @ColumnInfo("descriptorCategory")
    val descriptorCategory: Long
)
