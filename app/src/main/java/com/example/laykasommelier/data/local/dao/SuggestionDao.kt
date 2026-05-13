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
    fun getAllSuggestion(): Flow<List<Suggestion>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuggestion(suggestion: Suggestion): Long
    @Delete
    suspend fun deleteSuggestion(suggestion: Suggestion)

    @Query("DELETE FROM Suggestions")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Suggestion>)
}