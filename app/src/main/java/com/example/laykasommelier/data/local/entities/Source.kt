package com.example.laykasommelier.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.w3c.dom.Text

@Entity(
    tableName = "Sources"
)
data class Source(
    @PrimaryKey(autoGenerate = true)
    val sourceID: Long = 0,
    @ColumnInfo(name = "sourceName")
    val sourceName: String,
    @ColumnInfo(name = "sourceUrl")
    val sourceUrl: String
)
