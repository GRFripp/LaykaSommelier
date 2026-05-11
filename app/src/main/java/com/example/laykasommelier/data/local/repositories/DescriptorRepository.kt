package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.dao.DescriptorDao
import com.example.laykasommelier.data.local.entities.Descriptor
import com.example.laykasommelier.data.local.pojo.AdminListItem
import com.example.laykasommelier.data.local.pojo.DescriptorWithCategory
import kotlinx.coroutines.flow.Flow

class DescriptorRepository(val descriptorDao: DescriptorDao) {

    fun getAllDescriptorsWithCategories(): Flow<List<DescriptorWithCategory>> =
        descriptorDao.getAllDescriptorsWithCategoryFlow()

    fun getAllDescriptorsFlow(): Flow<List<Descriptor>> =
        descriptorDao.getAllDescriptors()

    fun getALDescriptorsFlow(): Flow<List<AdminListItem.ALDescriptor>> =
        descriptorDao.getALDescriptors()

    suspend fun getById(id: Long): Descriptor =
        descriptorDao.getEditDescriptorById(id) ?: throw Exception("Дескриптор не найден")

    suspend fun insert(descriptor: Descriptor): Long =
        descriptorDao.insertDescriptor(descriptor)

    suspend fun update(descriptor: Descriptor) =
        descriptorDao.updateDescriptor(descriptor)

    suspend fun delete(descriptor: Descriptor) =
        descriptorDao.deleteDescriptor(descriptor)
}