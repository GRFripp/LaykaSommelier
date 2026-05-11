package com.example.laykasommelier.data.local.repositories

import android.util.Log
import com.example.laykasommelier.data.local.dao.DescriptorReviewDao
import com.example.laykasommelier.data.local.dao.ReviewDao
import com.example.laykasommelier.data.local.entities.DescriptorReview
import com.example.laykasommelier.data.local.entities.Review
import com.example.laykasommelier.data.local.pojo.DescriptorChip
import com.example.laykasommelier.data.local.pojo.DrinkReviewItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReviewRepository(
    private val reviewDao: ReviewDao,
    private val descriptorReviewDao: DescriptorReviewDao
) {
    suspend fun insertReview(review: Review): Long = reviewDao.insertReview(review)

    suspend fun updateReview(review: Review) = reviewDao.updateReview(review)

    suspend fun deleteReviewById(id: Long) = reviewDao.getReviewById(id)

    suspend fun getReviewById(id: Long): Review =
        reviewDao.getReviewById(id) ?: throw Exception("Рецензия не найдена")

    fun getReviewsForDrink(drinkId: Long): Flow<List<DrinkReviewItem>> {
        return reviewDao.getReviewRowsByDrinkId(drinkId).map { rows ->
            rows.groupBy { it.reviewId }.map { (reviewId, group) ->
                val first = group.first()
                DrinkReviewItem(
                    reviewId = reviewId,
                    sourceName = first.sourceName,
                    sourceUrl = first.sourceUrl,
                    descriptors = group
                        .filter { it.descriptorName != null }
                        .map {
                            DescriptorChip(
                                it.descriptorName!!,
                                it.descriptorColor ?: "#888888"
                            )
                        }
                        .distinct()
                )
            }
        }
    }

    suspend fun getDescriptorIdsByReviewId(reviewId: Long): Set<Long> =
        descriptorReviewDao.getDescriptorIdsByReviewId(reviewId).toSet()

    suspend fun updateReviewDescriptors(reviewId: Long, descriptorIds: List<Long>) {
        // Удаляем старые связи
        descriptorReviewDao.deleteAllByReviewId(reviewId)
        Log.d("ReviewRepo", "Deleted old links for reviewId=$reviewId")

        descriptorIds.forEach { descriptorId ->
            try {
                Log.d("ReviewRepo", "Trying to insert link: reviewId=$reviewId, descriptorId=$descriptorId")
                descriptorReviewDao.insertLink(DescriptorReview(descriptorId, reviewId))
                Log.d("ReviewRepo", "Inserted successfully for descriptorId=$descriptorId")
            } catch (e: Exception) {
                Log.e("ReviewRepo", "FAILED to insert link for descriptorId=$descriptorId", e)
                // Не прерываем цикл, чтобы увидеть все проблемные дескрипторы
            }
        }
    }
}