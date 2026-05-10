package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.laykasommelier.data.local.entities.Ingredient
import com.example.laykasommelier.data.local.pojo.AdminListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Query("Select * from Ingredients")
    fun getAllIngredients(): Flow<List<Ingredient>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient): Long
    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    @Query("""
        Select ingredientID as id,ingredientName as name, ingredientAbv as abv
        From
        Ingredients
    """)
    fun getALIngredients(): Flow<List<AdminListItem.ALIngredient>>

    @Query("SELECT * FROM Ingredients WHERE ingredientID = :id")
    suspend fun getIngredientById(id: Long): Ingredient?

    @Update
    suspend fun updateIngredient(ingredient: Ingredient)

    @Query("DELETE FROM Ingredients WHERE ingredientID = :id")
    suspend fun deleteById(id: Long)
}