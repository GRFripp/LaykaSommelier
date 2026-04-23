package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laykasommelier.data.local.entities.IngredientDescriptor
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDescriptorDao {
    @Query("Select * from IngredientsDescriptors")
    fun getAllDrinks(): Flow<List<IngredientDescriptor>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(ingredientDescriptor: IngredientDescriptor): Long
    @Delete
    suspend fun deleteDrink(ingredientDescriptor: IngredientDescriptor)
}