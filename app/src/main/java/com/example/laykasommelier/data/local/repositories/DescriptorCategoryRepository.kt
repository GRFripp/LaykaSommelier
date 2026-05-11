package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.dao.DescriptorCategoryDao
import com.example.laykasommelier.data.local.entities.DescriptorCategory
import kotlinx.coroutines.flow.Flow

class DescriptorCategoryRepository(val categoryDao: DescriptorCategoryDao) {
    suspend fun getAllCategories(): List<DescriptorCategory> =
        categoryDao.getAllDescriptorCategories()
    suspend fun getById(id: Long): DescriptorCategory =
        categoryDao.getCategoryById(id) ?: throw Exception("Категория не найдена")

    suspend fun insert(category: DescriptorCategory): Long =
        categoryDao.insertDescriptorCategory(category)

    suspend fun update(category: DescriptorCategory) =
        categoryDao.updateCategory(category)
}