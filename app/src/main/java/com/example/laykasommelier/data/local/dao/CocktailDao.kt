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
        Select c.cocktailID as cId, c.cocktailName as cName, d.descriptorName as dName, dc.descriptorCategoryColor as dColor
        From 
        Cocktails as c left join CocktailsIngredients as ci on c.cocktailID = ci.cocktailID
        left join Ingredients as i on ci.ingredientID = i.ingredientID
        left join IngredientsDescriptors as id on i.ingredientID = id.ingredientID
        left join descriptors as d on id.ingredientID = d.descriptorID
        left join DescriptorCategories as dc on d.descriptorCategory = dc.descriptorCategoryID
        group by c.cocktailID, dc.descriptorCategoryColor
        order by c.cocktailName
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