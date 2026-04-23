package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laykasommelier.data.local.entities.Suggestion
import kotlinx.coroutines.flow.Flow

@Dao
interface SuggestionDao {
    @Query("Select * from Suggestions")
    fun getAllDrinks(): Flow<List<Suggestion>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(suggestion: Suggestion): Long
    @Delete
    suspend fun deleteDrink(suggestion: Suggestion)
}