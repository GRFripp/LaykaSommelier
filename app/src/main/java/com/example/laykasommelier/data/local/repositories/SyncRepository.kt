package com.example.laykasommelier.data.local.repositories
import com.example.laykasommelier.data.local.database.AppDatabase
import com.example.laykasommelier.data.local.mapper.*
import com.example.laykasommelier.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val apiService: ApiService,
    private val database: AppDatabase
) {
    suspend fun syncAll() {
        withContext(Dispatchers.IO) {
            // Drinks
            val drinks = apiService.getDrinks()
            database.drinkDao().deleteAll()
            database.drinkDao().insertAll(drinks.map { it.toEntity() })

            // MakingMethods
            val methods = apiService.getMakingMethods()
            database.makingMethodDao().deleteAll()
            database.makingMethodDao().insertAll(methods.map { it.toEntity() })

            // Cocktails
            val cocktails = apiService.getCocktails()
            database.cocktailDao().deleteAll()
            database.cocktailDao().insertAll(cocktails.map { it.toEntity() })

            // Ingredients
            val ingredients = apiService.getIngredients()
            database.ingredientDao().deleteAll()
            database.ingredientDao().insertAll(ingredients.map { it.toEntity() })

            // CocktailIngredients
            val ci = apiService.getCocktailIngredients()
            database.cocktailIngredientDao().deleteAll()
            database.cocktailIngredientDao().insertAll(ci.map { it.toEntity() })

            // DescriptorCategories
            val categories = apiService.getDescriptorCategories()
            database.descriptorCategoryDao().deleteAll()
            database.descriptorCategoryDao().insertAll(categories.map { it.toEntity() })

            // Descriptors
            val descriptors = apiService.getDescriptors()
            database.descriptorDao().deleteAll()
            database.descriptorDao().insertAll(descriptors.map { it.toEntity() })

            // IngredientDescriptors
            val idLinks = apiService.getIngredientDescriptors()
            database.ingredientDescriptorDao().deleteAll()
            database.ingredientDescriptorDao().insertAll(idLinks.map { it.toEntity() })

            // Sources
            val sources = apiService.getSources()
            database.sourceDao().deleteAll()
            database.sourceDao().insertAll(sources.map { it.toEntity() })

            // Reviews
            val reviews = apiService.getReviews()
            database.reviewDao().deleteAll()
            database.reviewDao().insertAll(reviews.map { it.toEntity() })

            // DescriptorReviews
            val dr = apiService.getDescriptorReviews()
            database.descriptorReviewDao().deleteAll()
            database.descriptorReviewDao().insertAll(dr.map { it.toEntity() })

            // Employees
            val employees = apiService.getEmployees()
            database.employeeDao().deleteAll()
            database.employeeDao().insertAll(employees.map { it.toEntity() })

            // Suggestions
            val suggestions = apiService.getSuggestions()
            database.suggestionDao().deleteAll()
            database.suggestionDao().insertAll(suggestions.map { it.toEntity() })
        }
    }
}