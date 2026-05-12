package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laykasommelier.data.local.entities.CocktailIngredient
import com.example.laykasommelier.data.local.entities.Drink
import com.example.laykasommelier.data.local.pojo.CocktailIngredientLinkItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CocktailIngredientDao {
    @Query("Select * from CocktailsIngredients")
    fun getAllDrinks(): Flow<List<CocktailIngredient>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(cocktailIngredient: CocktailIngredient): Long
    @Delete
    suspend fun deleteDrink(cocktailIngredient: CocktailIngredient)

    @Query("DELETE FROM CocktailsIngredients WHERE cocktailID = :cocktailId AND ingredientID = :ingredientId")
    suspend fun deleteLink(cocktailId: Long, ingredientId: Long)

    @Query("DELETE FROM CocktailsIngredients WHERE cocktailID = :cocktailId")
    suspend fun deleteAllLinksForCocktail(cocktailId: Long)

    @Query("""
    SELECT i.ingredientID AS ingredientId, 
           i.ingredientName AS ingredientName, 
           ci.ingredientVolume AS volume
    FROM CocktailsIngredients ci
    INNER JOIN Ingredients i ON ci.ingredientID = i.ingredientID
    WHERE ci.cocktailID = :cocktailId
    ORDER BY i.ingredientName
""")
    fun getIngredientLinks(cocktailId: Long): Flow<List<CocktailIngredientLinkItem>>
}