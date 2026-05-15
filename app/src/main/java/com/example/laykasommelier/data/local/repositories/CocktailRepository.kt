package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.dao.CocktailDao
import com.example.laykasommelier.data.local.dao.CocktailIngredientDao
import com.example.laykasommelier.data.local.dao.IngredientDao
import com.example.laykasommelier.data.local.dao.MakingMethodDao
import com.example.laykasommelier.data.local.entities.Cocktail
import com.example.laykasommelier.data.local.entities.CocktailIngredient
import com.example.laykasommelier.data.local.entities.Ingredient
import com.example.laykasommelier.data.local.entities.MakingMethod
import com.example.laykasommelier.data.local.pojo.CocktailDescriptors
import com.example.laykasommelier.data.local.pojo.CocktailIngredientItem
import com.example.laykasommelier.data.local.pojo.CocktailIngredientLinkItem
import com.example.laykasommelier.data.local.pojo.CocktailListRow
import kotlinx.coroutines.flow.Flow
import com.example.laykasommelier.data.local.pojo.CocktailListPreviews
import com.example.laykasommelier.data.local.pojo.DescriptorChip
import com.example.laykasommelier.data.local.pojo.DrinkListPreviews
import kotlinx.coroutines.flow.map

class CocktailRepository(val cocktailDao: CocktailDao,val ingredientDao: IngredientDao, val makingMethodDao: MakingMethodDao, val cocktailIngredientDao: CocktailIngredientDao) {
    fun getCocktailPreviews(): Flow<List<CocktailListPreviews>> {
        return cocktailDao.getCocktailListRows().map{ rows ->
            rows.groupBy {it.cId}
                .map {
                        (cId, cRows)->
                    val first = cRows.first()
                    CocktailListPreviews(
                        cId,
                        first.cName,
                        imageUrl = first.cImageUrl,
                        descriptors = cRows
                            .filter { it.dName != null && it.dColor != null }
                            .map{ CocktailDescriptors(it.dName!!,it.dColor!!) }
                    )
                }
        }

    }
    fun getCocktail(id: Long): Flow<Cocktail> = cocktailDao.getCocktailById(id)

    fun getCocktailIngredients(cocktailId: Long): Flow<List<CocktailIngredientItem>> {
        return ingredientDao.getIngredientRows(cocktailId).map { rows ->
            rows.groupBy { it.ingredientId }.map { (ingredientId, group) ->
                val first = group.first()
                CocktailIngredientItem(
                    ingredientId = ingredientId,
                    ingredientName = first.ingredientName,
                    ingredientAbv = first.ingredientAbv,
                    ingredientAcidity = first.ingredientAcidity,
                    ingredientSugarLevel = first.ingredientSugarLevel,
                    volumeInCocktail = first.volumeInCocktail,
                    descriptors = group
                        .filter { it.descriptorName != null }
                        .map {
                            DescriptorChip(
                                name = it.descriptorName!!,
                                color = it.descriptorColor ?: "#888888"
                            )
                        }
                        .distinct()
                )
            }
        }
    }
    fun getMakingMethod(id: Long): Flow<MakingMethod?> = makingMethodDao.getMethodById(id)

    suspend fun insertCocktail(cocktail: Cocktail): Long = cocktailDao.insertDrink(cocktail)
    suspend fun updateCocktail(cocktail: Cocktail) = cocktailDao.updateCocktail(cocktail)

    fun getCocktailIngredientsLinks(cocktailId: Long): Flow<List<CocktailIngredientLinkItem>> =
        cocktailIngredientDao.getIngredientLinks(cocktailId)

    suspend fun addIngredientLink(cocktailId: Long, ingredientId: Long, volume: Double) {
        cocktailIngredientDao.insertLink(CocktailIngredient(cocktailId, ingredientId, volume))
    }

    suspend fun removeIngredientLink(cocktailId: Long, ingredientId: Long) {
        cocktailIngredientDao.deleteLink(cocktailId, ingredientId)
    }

    suspend fun updateIngredientLinks(cocktailId: Long, links: List<CocktailIngredient>) {
        cocktailIngredientDao.deleteAllLinksForCocktail(cocktailId)
        links.forEach { cocktailIngredientDao.insertLink(it) }
    }

    fun getAllIngredients(): Flow<List<Ingredient>> = ingredientDao.getAllIngredients()
}




