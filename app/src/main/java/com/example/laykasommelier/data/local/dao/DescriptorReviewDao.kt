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
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLink(link: DescriptorReview): Long
    @Delete
    suspend fun deleteDescriptorReview(descriptorReview: DescriptorReview)

    @Query("DELETE FROM DescriptorsReviews WHERE reviewID = :reviewId")
    suspend fun deleteAllByReviewId(reviewId: Long)

    @Query("SELECT descriptorID FROM DescriptorsReviews WHERE reviewID = :reviewId")
    suspend fun getDescriptorIdsByReviewId(reviewId: Long): List<Long>
    @Query("DELETE FROM DescriptorsReviews")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<DescriptorReview>)
}