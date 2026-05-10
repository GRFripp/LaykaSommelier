package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.dao.DescriptorCategoryDao
import com.example.laykasommelier.data.local.entities.DescriptorCategory
import kotlinx.coroutines.flow.Flow

class DescriptorCategoryRepository(val categoryDao: DescriptorCategoryDao) {
    suspend fun getAllCategories(): List<DescriptorCategory> =
        categoryDao.getAllDescriptorCategories()
}