package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laykasommelier.data.local.entities.Descriptor
import com.example.laykasommelier.data.local.entities.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("Select * from Reviews")
    fun getAllReviews(): Flow<List<Review>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review): Long
    @Delete
    suspend fun deleteReview(review: Review)
}