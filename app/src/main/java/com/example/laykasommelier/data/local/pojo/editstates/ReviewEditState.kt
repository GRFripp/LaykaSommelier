package com.example.laykasommelier.data.local.pojo.editstates

data class ReviewEditState(
    val sourceId: Long = -1L,      // -1 – не выбран
    val url: String = "",
    val selectedDescriptorIds: Set<Long> = emptySet(),
    val searchQuery: String = "",
    val selectedCategoryId: Long? = null
)