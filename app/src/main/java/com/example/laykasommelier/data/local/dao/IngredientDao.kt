package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laykasommelier.data.local.entities.Ingredient
import com.example.laykasommelier.data.local.pojo.AdminListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Query("Select * from Ingredients")
    fun getAllDrinks(): Flow<List<Ingredient>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(ingredient: Ingredient): Long
    @Delete
    suspend fun deleteDrink(ingredient: Ingredient)

    @Query("""
        Select ingredientID as id,ingredientName as name, ingredientAbv as abv
        From
        Ingredients
    """)
    fun getALIngredients(): Flow<List<AdminListItem.ALIngredient>>
}