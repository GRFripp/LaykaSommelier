package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laykasommelier.data.local.entities.Descriptor
import com.example.laykasommelier.data.local.entities.Drink
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDao {
    @Query("Select * from Drinks")
    fun getAllDrinks(): Flow<List<Drink>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(drink: Drink): Long
    @Delete
    suspend fun deleteDrink(drink: Drink)
}