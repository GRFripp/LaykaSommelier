package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.laykasommelier.data.local.entities.Source
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceDao {
    @Query("Select * from Sources order by sourceName") suspend fun getAllSources(): List<Source>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: Source): Long
    @Delete
    suspend fun deleteSource(source: Source)

    @Query("SELECT * FROM Sources WHERE sourceID = :id")
    suspend fun getSourceById(id: Long): Source?
    @Update
    suspend fun updateSource(source: Source)

    @Query("DELETE FROM Sources")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Source>)
}