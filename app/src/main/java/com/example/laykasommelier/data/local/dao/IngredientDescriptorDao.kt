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

    @Query("SELECT descriptorId FROM IngredientsDescriptors WHERE ingredientID = :ingredientId")
    suspend fun getDescriptorIdsByIngredientId(ingredientId: Long): List<Long>


    @Query("DELETE FROM IngredientsDescriptors WHERE ingredientId = :ingredientId")
    suspend fun deleteAllDescriptorsByIngredientId(ingredientId: Long)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDescriptorLink(link: IngredientDescriptor)
    @Query("DELETE FROM IngredientsDescriptors")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<IngredientDescriptor>)
}