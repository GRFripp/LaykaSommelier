package com.example.laykasommelier.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "DescriptorsReviews",
    primaryKeys = ["descriptorID","reviewID"],
    foreignKeys = [
        ForeignKey(
            Descriptor::class,
            parentColumns = ["descriptorID"],
            ["descriptorID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            Review::class,
            parentColumns = ["reviewID"],
            ["reviewID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DescriptorReview(
    val descriptorID : Int,
    val reviewID:Int
)
