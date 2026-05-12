package com.example.laykasommelier.data.local.di

import android.content.Context
import com.example.laykasommelier.data.local.dao.*
import com.example.laykasommelier.data.local.database.AppDatabase
import com.example.laykasommelier.data.local.repositories.AdminRepository
import com.example.laykasommelier.data.local.repositories.CocktailRepository
import com.example.laykasommelier.data.local.repositories.DescriptorCategoryRepository
import com.example.laykasommelier.data.local.repositories.DescriptorRepository
import com.example.laykasommelier.data.local.repositories.DrinkRepository
import com.example.laykasommelier.data.local.repositories.EmployeeRepository
import com.example.laykasommelier.data.local.repositories.IngredientRepository
import com.example.laykasommelier.data.local.repositories.MakingMethodRepository
import com.example.laykasommelier.data.local.repositories.ReviewRepository
import com.example.laykasommelier.data.local.repositories.SourceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDB(@ApplicationContext context: Context): AppDatabase = AppDatabase.getInstance(context)

    //Предоставление элементов Dao через базу данных
    @Provides
    fun provideCocktailDao(db: AppDatabase): CocktailDao = db.cocktailDao()
    @Provides
    fun provideCocktailIngredientDao(db: AppDatabase): CocktailIngredientDao = db.cocktailIngredientDao()
    @Provides
    fun provideDescriptorCategoryDao(db: AppDatabase): DescriptorCategoryDao = db.descriptorCategoryDao()
    @Provides
    fun provideDescriptorDao(db: AppDatabase): DescriptorDao = db.descriptorDao()
    @Provides
    fun provideDescriptorReviewDao(db: AppDatabase): DescriptorReviewDao = db.descriptorReviewDao()
    @Provides
    fun provideDrinkDao(db: AppDatabase): DrinkDao = db.drinkDao()
    @Provides
    fun provideEmployeeDao(db: AppDatabase): EmployeeDao = db.employeeDao()
    @Provides
    fun provideIngredientDao(db: AppDatabase): IngredientDao = db.ingredientDao()
    @Provides
    fun provideIngredientDescriptorDao(db: AppDatabase): IngredientDescriptorDao = db.ingredientDescriptorDao()
    @Provides
    fun provideMakingMethodDao(db: AppDatabase): MakingMethodDao = db.makingMethodDao()
    @Provides
    fun provideReviewDao(db: AppDatabase): ReviewDao = db.reviewDao()
    @Provides
    fun provideSourceDao(db: AppDatabase): SourceDao = db.sourceDao()
    @Provides
    fun provideSuggestionDao(db: AppDatabase): SuggestionDao = db.suggestionDao()

    //Предоставление классов-Репозиториев, задествующих Dao
    @Provides
    @Singleton
    fun provideDrinkRepository(drinkDao: DrinkDao): DrinkRepository= DrinkRepository(drinkDao)

    @Provides
    @Singleton
    fun provideAdminRepo(employeeDao: EmployeeDao,descriptorDao: DescriptorDao,descriptorCategoryDao: DescriptorCategoryDao,makingMethodDao: MakingMethodDao,ingredientDao: IngredientDao):
            AdminRepository=
        AdminRepository(employeeDao,descriptorDao,descriptorCategoryDao,makingMethodDao,ingredientDao)
    @Provides
    @Singleton
    fun provideCocktailRepo(cocktailDao: CocktailDao, ingredientDao: IngredientDao, makingMethodDao: MakingMethodDao, cocktailIngredientDao: CocktailIngredientDao): CocktailRepository =
        CocktailRepository(cocktailDao,ingredientDao, makingMethodDao, cocktailIngredientDao)

    @Provides
    @Singleton
    fun provideEmployeeRepo(employeeDao: EmployeeDao): EmployeeRepository =
        EmployeeRepository(employeeDao)

    @Provides
    @Singleton
    fun provideIngredientRepo(ingredientDao: IngredientDao, ingredientDescriptorDao: IngredientDescriptorDao): IngredientRepository=
        IngredientRepository(ingredientDao, ingredientDescriptorDao)

    @Provides
    @Singleton
    fun provideDescriptorRepo(descriptorDao: DescriptorDao): DescriptorRepository=
        DescriptorRepository(descriptorDao)
    @Provides
    @Singleton
    fun provideDescriptorCategoryRepo(descriptorCategoryDao: DescriptorCategoryDao): DescriptorCategoryRepository=
        DescriptorCategoryRepository(descriptorCategoryDao)

    @Provides
    @Singleton
    fun provideMethodRepo(makingMethodDao: MakingMethodDao): MakingMethodRepository=
        MakingMethodRepository(makingMethodDao)

    @Provides
    @Singleton
    fun provideReviewRepo(reviewDao: ReviewDao, descriptorReviewDao: DescriptorReviewDao): ReviewRepository =
        ReviewRepository(reviewDao, descriptorReviewDao)
    @Provides
    @Singleton
    fun provideSourceRepo(sourceDao: SourceDao): SourceRepository=
        SourceRepository(sourceDao)
}