package com.example.laykasommelier.network.dto

data class DescriptorCategoryCreateRequest(
    val name: String,
    val color: String
)

data class DescriptorCategoryUpdateRequest(
    val name: String,
    val color: String
)

data class DescriptorCreateRequest(
    val name: String,
    val categoryId: Long
)

data class DescriptorUpdateRequest(
    val name: String,
    val categoryId: Long
)

data class IngredientDescriptorLinkRequest(
    val ingredientId: Long,
    val descriptorId: Long
)
data class SourceCreateRequest(
    val name: String,
    val url: String
)

// ReviewCreateRequest
data class ReviewCreateRequest(
    val reviewedDrinkId: Long,
    val sourceId: Long,
    val url: String?
)

// DescriptorReviewLinkRequest
data class DescriptorReviewLinkRequest(
    val descriptorId: Long,
    val reviewId: Long
)

// EmployeeCreateRequest
data class EmployeeCreateRequest(
    val name: String,
    val password: String,
    val position: String
)

// SuggestionCreateRequest
data class SuggestionCreateRequest(
    val cocktailId: Long,
    val employeeId: Long,
    val status: String
)