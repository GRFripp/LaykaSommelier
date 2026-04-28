package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.database.AppDatabase
import com.example.laykasommelier.data.local.pojo.DrinkListTypes
import kotlinx.coroutines.flow.Flow
import com.example.laykasommelier.data.local.dao.DrinkDao

class DrinkRepository(private val  drinkDao: DrinkDao) {
    fun drinkListTypes(): Flow<List<DrinkListTypes>> = drinkDao.getDrinkTypes()
}