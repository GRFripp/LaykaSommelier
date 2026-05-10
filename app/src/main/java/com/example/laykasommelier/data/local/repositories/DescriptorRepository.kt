package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.dao.DescriptorDao
import com.example.laykasommelier.data.local.pojo.DescriptorWithCategory
import kotlinx.coroutines.flow.Flow

class DescriptorRepository(val descriptorDao: DescriptorDao) {

    fun getAllDescriptorsWithCategories(): Flow<List<DescriptorWithCategory>> =
        descriptorDao.getAllDescriptorsWithCategoryFlow()


}