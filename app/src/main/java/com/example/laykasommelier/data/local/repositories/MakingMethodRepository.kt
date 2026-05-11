package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.dao.MakingMethodDao
import com.example.laykasommelier.data.local.entities.MakingMethod
import com.example.laykasommelier.data.local.pojo.AdminListItem
import kotlinx.coroutines.flow.Flow

class MakingMethodRepository(private val methodDao: MakingMethodDao) {
    fun getAllMethodsFlow(): Flow<List<MakingMethod>> = methodDao.getAllMakingMethods()

    fun getALMethodsFlow(): Flow<List<AdminListItem.ALMakingMethod>> =
        methodDao.getALMakingMethods()

    suspend fun getById(id: Long): MakingMethod =
        methodDao.getMethodById(id) ?: throw Exception("Метод не найден")

    suspend fun insert(method: MakingMethod): Long = methodDao.insertMakingMethods(method)

    suspend fun update(method: MakingMethod) = methodDao.updateMethod(method)

    suspend fun delete(method: MakingMethod) = methodDao.deleteMakingMethods(method)
}