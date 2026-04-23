package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laykasommelier.data.local.entities.CocktailIngredient
import com.example.laykasommelier.data.local.entities.Drink
import kotlinx.coroutines.flow.Flow

@Dao
interface CocktailIngredientDao {
    @Query("Select * from CocktailsIngredients")
    fun getAllDrinks(): Flow<List<CocktailIngredient>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(cocktailIngredient: CocktailIngredient): Long
    @Delete
    suspend fun deleteDrink(cocktailIngredient: CocktailIngredient)
}