package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laykasommelier.data.local.entities.Cocktail
import kotlinx.coroutines.flow.Flow

@Dao
interface CocktailDao {
    @Query("Select * from Cocktails")
    fun getAllDrinks(): Flow<List<Cocktail>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(cocktail: Cocktail): Long
    @Delete
    suspend fun deleteDrink(cocktail: Cocktail)
}