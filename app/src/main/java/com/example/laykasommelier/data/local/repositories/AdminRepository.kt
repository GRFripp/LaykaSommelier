package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.dao.DescriptorCategoryDao
import com.example.laykasommelier.data.local.dao.DescriptorDao
import com.example.laykasommelier.data.local.dao.EmployeeDao
import com.example.laykasommelier.data.local.dao.IngredientDao
import com.example.laykasommelier.data.local.dao.MakingMethodDao
import com.example.laykasommelier.data.local.pojo.AdminListItem
import kotlinx.coroutines.flow.Flow

class AdminRepository(private val employeeDao: EmployeeDao,
    private val descriptorDao: DescriptorDao,
    private val descriptorCategoryDao: DescriptorCategoryDao,
    private val makingMethodDao: MakingMethodDao,
    private val ingredientDao: IngredientDao
    ) {
    fun getEmployeesAdmin(): Flow<List<AdminListItem.ALEmployee>> = employeeDao.getALEmployees()
    fun getIngredientAdmin(): Flow<List<AdminListItem.ALIngredient>> = ingredientDao.getALIngredients()
    fun getDescriptorAdmin(): Flow<List<AdminListItem.ALDescriptor>> = descriptorDao.getALDescriptors()
    fun getDescriptorCategoriesAdmin(): Flow<List<AdminListItem.ALDescriptorCategory>> = descriptorCategoryDao.getALDescriptorCategories()
    fun getMakingMethodsAdmin(): Flow<List<AdminListItem.ALMakingMethod>> = makingMethodDao.getALMakingMethods()

}