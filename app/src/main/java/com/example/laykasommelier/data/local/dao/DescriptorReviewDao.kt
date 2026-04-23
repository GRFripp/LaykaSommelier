package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laykasommelier.data.local.entities.DescriptorReview
import kotlinx.coroutines.flow.Flow

@Dao
interface DescriptorReviewDao {
    @Query("Select * from DescriptorsReviews")
    fun getAllDescriptorsReviews(): Flow<List<DescriptorReview>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDescriptorReview(descriptorReview: DescriptorReview): Long
    @Delete
    suspend fun deleteDescriptorReview(descriptorReview: DescriptorReview)
}