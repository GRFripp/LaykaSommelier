package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.laykasommelier.data.local.entities.Cocktail
import com.example.laykasommelier.data.local.pojo.CocktailListRow
import kotlinx.coroutines.flow.Flow

@Dao
interface CocktailDao {
    @Query("Select * from Cocktails")
    fun getAllDrinks(): Flow<List<Cocktail>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(cocktail: Cocktail): Long
    @Delete
    suspend fun deleteDrink(cocktail: Cocktail)

    @Query("""
    SELECT c.cocktailID AS cId, c.cocktailName AS cName,
           d.descriptorName AS dName, dc.descriptorCategoryColor AS dColor, c.cocktailImageUrl as cImageUrl
    FROM Cocktails c
    LEFT JOIN CocktailsIngredients ci ON c.cocktailID = ci.cocktailID
    LEFT JOIN Ingredients i ON ci.ingredientID = i.ingredientID
    LEFT JOIN IngredientsDescriptors id ON i.ingredientID = id.ingredientID
    LEFT JOIN Descriptors d ON id.descriptorID = d.descriptorID
    LEFT JOIN DescriptorCategories dc ON d.descriptorCategory = dc.descriptorCategoryID
    LEFT JOIN Suggestions s ON c.cocktailID = s.suggestedCocktailID
    WHERE s.suggestionStatus != 'pending' OR s.suggestionID IS NULL
    GROUP BY c.cocktailID, dc.descriptorCategoryColor
    ORDER BY c.cocktailName
""")
     fun getCocktailListRows(): Flow<List<CocktailListRow>>
    @Query("SELECT * FROM Cocktails WHERE cocktailID = :id")
    fun getCocktailById(id: Long): Flow<Cocktail>

    @Update
    suspend fun updateCocktail(cocktail: Cocktail)

    @Query("DELETE FROM Cocktails")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cocktails: List<Cocktail>)
}