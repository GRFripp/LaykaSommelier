package com.example.laykasommelier.network

import retrofit2.http.GET
import com.example.laykasommelier.network.dto.*
import okhttp3.MultipartBody
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @GET("api/drinks")
    suspend fun getDrinks(): List<DrinkDto>

    @GET("api/makingmethods")
    suspend fun getMakingMethods(): List<MakingMethodDto>

    @GET("api/cocktails")
    suspend fun getCocktails(): List<CocktailDto>

    @GET("api/ingredients")
    suspend fun getIngredients(): List<IngredientDto>

    @GET("api/cocktailingredients")
    suspend fun getCocktailIngredients(): List<CocktailIngredientDto>

    @GET("api/descriptorcategories")
    suspend fun getDescriptorCategories(): List<DescriptorCategoryDto>

    @GET("api/descriptors")
    suspend fun getDescriptors(): List<DescriptorDto>

    @GET("api/ingredientdescriptors")
    suspend fun getIngredientDescriptors(): List<IngredientDescriptorDto>

    @GET("api/sources")
    suspend fun getSources(): List<SourceDto>

    @GET("api/reviews")
    suspend fun getReviews(): List<ReviewDto>

    @GET("api/descriptorreviews")
    suspend fun getDescriptorReviews(): List<DescriptorReviewDto>

    @GET("api/employees")
    suspend fun getEmployees(): List<EmployeeDto>

    @GET("api/suggestions")
    suspend fun getSuggestions(): List<SuggestionDto>

    // Аутентификация
    @POST("api/auth/verify")
    suspend fun verifyToken(@Body request: TokenRequest): VerifyTokenResponse
    @Multipart
    @POST("api/images/upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): ImageUploadResponse

    @POST("api/drinks")
    suspend fun createDrink(@Body drink: DrinkCreateRequest): DrinkDto

    @PUT("api/drinks/{id}")
    suspend fun updateDrink(@Path("id") id: Long, @Body drink: DrinkCreateRequest)

    @DELETE("api/drinks/{id}")
    suspend fun deleteDrink(@Path("id") id: Long)

    // Cocktails
    @POST("api/cocktails")
    suspend fun createCocktail(@Body cocktail: CocktailCreateRequest): CocktailDto

    @PUT("api/cocktails/{id}")
    suspend fun updateCocktail(@Path("id") id: Long, @Body cocktail: CocktailUpdateRequest)

    @DELETE("api/cocktails/{id}")
    suspend fun deleteCocktail(@Path("id") id: Long)

    // Ingredients
    @POST("api/ingredients")
    suspend fun createIngredient(@Body ingredient: IngredientCreateRequest): IngredientDto

    @PUT("api/ingredients/{id}")
    suspend fun updateIngredient(@Path("id") id: Long, @Body ingredient: IngredientUpdateRequest)

    @DELETE("api/ingredients/{id}")
    suspend fun deleteIngredient(@Path("id") id: Long)

    // CocktailIngredients
    @POST("api/cocktailingredients")
    suspend fun addIngredientToCocktail(@Body link: CocktailIngredientLinkRequest): CocktailIngredientDto

    @DELETE("api/cocktailingredients/{cocktailId}/{ingredientId}")
    suspend fun removeIngredientFromCocktail(@Path("cocktailId") cocktailId: Long, @Path("ingredientId") ingredientId: Long)

    // MakingMethods
    @POST("api/makingmethods")
    suspend fun createMakingMethod(@Body method: MakingMethodCreateRequest): MakingMethodDto

    @PUT("api/makingmethods/{id}")
    suspend fun updateMakingMethod(@Path("id") id: Long, @Body method: MakingMethodCreateRequest)

    @DELETE("api/makingmethods/{id}")
    suspend fun deleteMakingMethod(@Path("id") id: Long)

    @POST("api/descriptorcategories")
    suspend fun createDescriptorCategory(@Body request: DescriptorCategoryCreateRequest): DescriptorCategoryDto

    @PUT("api/descriptorcategories/{id}")
    suspend fun updateDescriptorCategory(@Path("id") id: Long, @Body request: DescriptorCategoryUpdateRequest)

    @DELETE("api/descriptorcategories/{id}")
    suspend fun deleteDescriptorCategory(@Path("id") id: Long)

    // Descriptors
    @POST("api/descriptors")
    suspend fun createDescriptor(@Body request: DescriptorCreateRequest): DescriptorDto

    @PUT("api/descriptors/{id}")
    suspend fun updateDescriptor(@Path("id") id: Long, @Body request: DescriptorUpdateRequest)

    @DELETE("api/descriptors/{id}")
    suspend fun deleteDescriptor(@Path("id") id: Long)

    // IngredientDescriptors
    @POST("api/ingredientdescriptors")
    suspend fun addIngredientDescriptor(@Body request: IngredientDescriptorLinkRequest): IngredientDescriptorDto

    @DELETE("api/ingredientdescriptors/{ingredientId}/{descriptorId}")
    suspend fun removeIngredientDescriptor(@Path("ingredientId") ingredientId: Long, @Path("descriptorId") descriptorId: Long)

    @POST("api/sources")
    suspend fun createSource(@Body request: SourceCreateRequest): SourceDto

    @PUT("api/sources/{id}")
    suspend fun updateSource(@Path("id") id: Long, @Body request: SourceCreateRequest)

    @DELETE("api/sources/{id}")
    suspend fun deleteSource(@Path("id") id: Long)

    // Reviews
    @POST("api/reviews")
    suspend fun createReview(@Body request: ReviewCreateRequest): ReviewDto

    @PUT("api/reviews/{id}")
    suspend fun updateReview(@Path("id") id: Long, @Body request: ReviewCreateRequest)

    @DELETE("api/reviews/{id}")
    suspend fun deleteReview(@Path("id") id: Long)

    // DescriptorReviews
    @POST("api/descriptorreviews")
    suspend fun addDescriptorReview(@Body request: DescriptorReviewLinkRequest): DescriptorReviewDto

    @DELETE("api/descriptorreviews/{descriptorId}/{reviewId}")
    suspend fun removeDescriptorReview(@Path("descriptorId") descriptorId: Long, @Path("reviewId") reviewId: Long)

    // Employees
    @POST("api/employees")
    suspend fun createEmployee(@Body request: EmployeeCreateRequest): EmployeeDto

    @PUT("api/employees/{id}")
    suspend fun updateEmployee(@Path("id") id: Long, @Body request: EmployeeCreateRequest)

    @DELETE("api/employees/{id}")
    suspend fun deleteEmployee(@Path("id") id: Long)

    // Suggestions
    @POST("api/suggestions")
    suspend fun createSuggestion(@Body request: SuggestionCreateRequest): SuggestionDto

    @PUT("api/suggestions/{id}")
    suspend fun updateSuggestion(@Path("id") id: Long, @Body request: SuggestionCreateRequest)

    @DELETE("api/suggestions/{id}")
    suspend fun deleteSuggestion(@Path("id") id: Long)
}

data class TokenRequest(val token: String)
data class VerifyTokenResponse(val uid: String, val email: String)

data class ImageUploadResponse(val url: String)