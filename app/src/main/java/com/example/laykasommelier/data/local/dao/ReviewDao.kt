package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.laykasommelier.data.local.entities.Review
import com.example.laykasommelier.data.local.pojo.ReviewRow
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("Select * from Reviews")
    fun getAllReviews(): Flow<List<Review>>
    @Insert
    suspend fun insertReview(review: Review): Long
    @Delete
    suspend fun deleteReview(review: Review)

    @Update
    suspend fun updateReview(review: Review)

    @Query("SELECT * FROM Reviews WHERE reviewID = :id")
    suspend fun getReviewById(id: Long): Review?

    @Query("""
    SELECT r.reviewID AS reviewId,
           s.sourceName AS sourceName,
           s.sourceUrl AS sourceUrl,
           d.descriptorName AS descriptorName,
           dc.descriptorCategoryColor AS descriptorColor
    FROM Reviews r
    INNER JOIN Sources s ON r.reviewSourceID = s.sourceID
    LEFT JOIN DescriptorsReviews dr ON r.reviewID = dr.reviewID
    LEFT JOIN Descriptors d ON dr.descriptorID = d.descriptorID
    LEFT JOIN DescriptorCategories dc ON d.descriptorCategory = dc.descriptorCategoryID
    WHERE r.reviewedDrinkID = :drinkId
    ORDER BY r.reviewID, d.descriptorName
""")
    fun getReviewRowsByDrinkId(drinkId: Long): Flow<List<ReviewRow>>
}