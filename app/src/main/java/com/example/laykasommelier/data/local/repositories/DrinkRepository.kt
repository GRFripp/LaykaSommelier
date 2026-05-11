package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.pojo.DrinkListTypes
import kotlinx.coroutines.flow.Flow
import com.example.laykasommelier.data.local.dao.DrinkDao
import com.example.laykasommelier.data.local.entities.Drink
import com.example.laykasommelier.data.local.pojo.CategoryColor
import com.example.laykasommelier.data.local.pojo.DescriptorChip
import com.example.laykasommelier.data.local.pojo.DrinkDetail
import com.example.laykasommelier.data.local.pojo.DrinkListPreviews
import com.example.laykasommelier.data.local.pojo.DrinkReviewItem
import kotlinx.coroutines.flow.map

class DrinkRepository(private val  drinkDao: DrinkDao) {
    fun drinkListTypes(): Flow<List<DrinkListTypes>> = drinkDao.getDrinkTypes()

    fun drinkListPreviews(type:String): Flow<List<DrinkListPreviews>>{
        return drinkDao.getDrinkListRow(type).map{ rows ->
            rows.groupBy { it.drinkId }
                .map{ (drinkId, drinkRows) ->
                    val first = drinkRows.first()
                    DrinkListPreviews(
                        drinkId = drinkId,
                        drinkName = first.drinkName,
                        imageUrl = first.imageUrl,
                        categories = drinkRows
                            .filter{it.desCategoryName  != null}
                            .map{ CategoryColor(it.desCategoryName!!, it.desCategoryColor, it.desCategoryCount)}
                    )
                }
        }
    }
    fun getDrinkReviews(drinkId: Long): Flow<List<DrinkReviewItem>> {
        return drinkDao.getReviewRows(drinkId).map { rows ->
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
    fun getDrinkById(drinkId: Long): Flow<DrinkDetail> = drinkDao.getDrinkById(drinkId)

    suspend fun insertDrink(drink: Drink): Long = drinkDao.insertDrink(drink)

    suspend fun updateDrink(drink: Drink) = drinkDao.updateDrink(drink)
}