package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.dao.IngredientDao
import com.example.laykasommelier.data.local.dao.IngredientDescriptorDao
import com.example.laykasommelier.data.local.entities.Ingredient
import com.example.laykasommelier.data.local.entities.IngredientDescriptor
import javax.inject.Inject

class IngredientRepository @Inject constructor(
    private val ingredientDao: IngredientDao,
    private val ingredientDescriptorDao: IngredientDescriptorDao
) {
    suspend fun getById(id: Long): Ingredient =
        ingredientDao.getIngredientById(id) ?: throw Exception("Ингредиент не найден")

    suspend fun insertIngredient(ingredient: Ingredient): Long =
        ingredientDao.insertIngredient(ingredient)

    suspend fun updateIngredient(ingredient: Ingredient) =
        ingredientDao.updateIngredient(ingredient)

    suspend fun getDescriptorIdsForIngredient(ingredientId: Long): Set<Long> =
        ingredientDescriptorDao.getDescriptorIdsByIngredientId(ingredientId).toSet()

    suspend fun updateIngredientDescriptors(ingredientId: Long, descriptorIds: List<Long>) {
        ingredientDescriptorDao.deleteAllDescriptorsByIngredientId(ingredientId)
        descriptorIds.forEach { descriptorId ->
            ingredientDescriptorDao.insertDescriptorLink(
                IngredientDescriptor(ingredientId, descriptorId)
            )
        }
    }
}