package com.example.laykasommelier.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.laykasommelier.data.local.dao.*
import com.example.laykasommelier.data.local.entities.*

@Database(
    entities = [
        Cocktail::class,
        CocktailIngredient::class,
        Drink::class,
        Descriptor::class,
        DescriptorCategory::class,
        DescriptorReview::class,
        Employee::class,
        Ingredient::class,
        IngredientDescriptor::class,
        MakingMethod::class,
        Review::class,
        Source::class,
        Suggestion::class
               ],
    version = 10,
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun cocktailDao(): CocktailDao
    abstract fun cocktailIngredientDao(): CocktailIngredientDao
    abstract fun descriptorCategoryDao(): DescriptorCategoryDao
    abstract fun descriptorDao(): DescriptorDao

    abstract fun descriptorReviewDao(): DescriptorReviewDao
    abstract fun drinkDao(): DrinkDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun ingredientDescriptorDao(): IngredientDescriptorDao
    abstract fun makingMethodDao(): MakingMethodDao
    abstract fun reviewDao(): ReviewDao
    abstract  fun sourceDao(): SourceDao
    abstract fun suggestionDao(): SuggestionDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "layka_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}