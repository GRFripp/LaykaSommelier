package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.pojo.DrinkListTypes
import kotlinx.coroutines.flow.Flow
import com.example.laykasommelier.data.local.dao.DrinkDao
import com.example.laykasommelier.data.local.pojo.CategoryColor
import com.example.laykasommelier.data.local.pojo.DrinkListPreviews
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
}