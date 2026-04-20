package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laykasommelier.data.local.entities.Source
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceDao {
    @Query("Select * from Sources")
    fun getAllSources(): Flow<List<Source>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: Source): Long
    @Delete
    suspend fun deleteDescriptor(source: Source)
}