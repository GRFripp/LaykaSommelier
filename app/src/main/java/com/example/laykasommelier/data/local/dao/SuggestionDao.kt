package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.laykasommelier.data.local.entities.Suggestion
import com.example.laykasommelier.data.local.pojo.SuggestionRow
import kotlinx.coroutines.flow.Flow

@Dao
interface SuggestionDao {
    @Query("Select * from Suggestions")
    fun getAllSuggestion(): Flow<List<Suggestion>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuggestion(suggestion: Suggestion): Long
    @Delete
    suspend fun deleteSuggestion(suggestion: Suggestion)
    @Query("SELECT * FROM Suggestions WHERE suggestionID = :id")
    fun getSuggestionById(id: Long): Flow<Suggestion?>
    @Query("DELETE FROM Suggestions")
    suspend fun deleteAll()
        @Update
    suspend fun updateSuggestion(suggestion: Suggestion)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Suggestion>)
    @Query("UPDATE Suggestions SET suggestionStatus = :status WHERE suggestionID = :id")
    suspend fun updateSuggestionStatus(id: Long, status: String)

        @Query("""
        SELECT s.suggestionID AS suggestionId,
               c.cocktailID AS cocktailId,
               c.cocktailName AS cocktailName,
               
               e.employeeName AS employeeName,
               s.suggestionStatus AS status,
               d.descriptorName AS descriptorName,
               dc.descriptorCategoryColor AS descriptorColor,
               c.cocktailImageUrl as cocktailImageUrl
        FROM Suggestions s
        INNER JOIN Cocktails c ON s.suggestedCocktailID = c.cocktailID
        INNER JOIN Employees e ON s.suggestionEmployeeID = e.employeeID
        LEFT JOIN CocktailsIngredients ci ON c.cocktailID = ci.cocktailID
        LEFT JOIN Ingredients i ON ci.ingredientID = i.ingredientID
        LEFT JOIN IngredientsDescriptors idl ON i.ingredientID = idl.ingredientID
        LEFT JOIN Descriptors d ON idl.descriptorID = d.descriptorID
        LEFT JOIN DescriptorCategories dc ON d.descriptorCategory = dc.descriptorCategoryID
        ORDER BY s.suggestionID, d.descriptorName
    """)
        fun getSuggestionRows(): Flow<List<SuggestionRow>>

}