package com.example.laykasommelier.data.local.pojo

data class DrinkReviewItem(
    val reviewId: Long,
    val sourceName: String,
    val sourceUrl: String?,
    val descriptors: List<DescriptorChip>
)

data class DescriptorChip(
    val name: String,
    val color: String
)
