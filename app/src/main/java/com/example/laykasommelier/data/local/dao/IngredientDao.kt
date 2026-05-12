package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.laykasommelier.data.local.entities.Ingredient
import com.example.laykasommelier.data.local.pojo.AdminListItem
import com.example.laykasommelier.data.local.pojo.IngredientRow
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Query("Select * from Ingredients")
    fun getAllIngredients(): Flow<List<Ingredient>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient): Long
    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    @Query("""
        Select ingredientID as id,ingredientName as name, ingredientAbv as abv
        From
        Ingredients
    """)
    fun getALIngredients(): Flow<List<AdminListItem.ALIngredient>>

    @Query("SELECT * FROM Ingredients WHERE ingredientID = :id")
    suspend fun getIngredientById(id: Long): Ingredient?

    @Update
    suspend fun updateIngredient(ingredient: Ingredient)

    @Query("DELETE FROM Ingredients WHERE ingredientID = :id")
    suspend fun deleteById(id: Long)

    @Query("""
        SELECT ci.ingredientID AS ingredientId,
               i.ingredientName AS ingredientName,
               i.ingredientAbv AS ingredientAbv,
               i.ingredientAcidity AS ingredientAcidity,
               i.ingredientSugarLevel AS ingredientSugarLevel,
               ci.ingredientVolume AS volumeInCocktail,
               d.descriptorName AS descriptorName,
               dc.descriptorCategoryColor AS descriptorColor
        FROM CocktailsIngredients ci
        INNER JOIN Ingredients i ON ci.ingredientID = i.ingredientID
        LEFT JOIN IngredientsDescriptors idl ON i.ingredientID = idl.ingredientID
        LEFT JOIN Descriptors d ON idl.descriptorID = d.descriptorID
        LEFT JOIN DescriptorCategories dc ON d.descriptorCategory = dc.descriptorCategoryID
        WHERE ci.cocktailID = :cocktailId
        ORDER BY i.ingredientName, d.descriptorName
    """)
    fun getIngredientRows(cocktailId: Long): Flow<List<IngredientRow>>


}