package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import com.example.laykasommelier.data.local.entities.Drink
import com.example.laykasommelier.data.local.pojo.DrinkDetail
import com.example.laykasommelier.data.local.pojo.DrinkListRow
import com.example.laykasommelier.data.local.pojo.DrinkListTypes
import com.example.laykasommelier.data.local.pojo.ReviewRow
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDao {
    @Query("Select * from Drinks")
    fun getAllDrinks(): Flow<List<Drink>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(drink: Drink): Long
    @Delete
    suspend fun deleteDrink(drink: Drink)

    @Query(value=
        """
            Select drinkType as drinkListType, Count(*) as drinkListTypeCount 
            From Drinks
            Group by drinkType
            Order by drinkType
        """
    )
    fun getDrinkTypes(): Flow<List<DrinkListTypes>>

    @Query(value=
    """
        Select d.drinkID as drinkId, d.drinkName as drinkName, d.drinkImageUrl as imageUrl, Count(*) as desCategoryCount, dc.descriptorCategoryName as desCategoryName, dc.descriptorCategoryColor as desCategoryColor
        From Drinks as d
        left join Reviews as r on d.drinkID = r.reviewedDrinkID
        left join DescriptorsReviews as dr on dr.reviewID = r.reviewID
        left join Descriptors as des on des.descriptorID = dr.descriptorID
        left join DescriptorCategories as dc on dc.descriptorCategoryID = des.descriptorCategory
        where d.drinkType = :drinkType
        group by d.drinkID, dc.descriptorCategoryName, dc.descriptorCategoryColor
        order by d.drinkName
    """)
    fun getDrinkListRow(drinkType: String): Flow<List<DrinkListRow>>

    @Query("""
        Select drinkName as name,drinkType as type,drinkSubType as subType,drinkCountry as country,drinkProducer as producer,drinkAged as aged,drinkAbv as abv
        from Drinks
        Where Drinks.drinkID = :drinkId
    """)
    fun getDrinkById(drinkId: Long): Flow<DrinkDetail>

    @Query("""
    SELECT r.reviewID AS reviewId,
           s.sourceName AS sourceName,
           s.sourceUrl AS sourceUrl,
           d.descriptorName AS descriptorName,
           dc.descriptorCategoryColor AS descriptorColor
    FROM Reviews r
    INNER JOIN Sources s ON r.reviewSourceID = s.sourceID
    LEFT JOIN DescriptorsReviews dr ON r.reviewID = dr.reviewID
    LEFT JOIN Descriptors d ON dr.descriptorID = d.descriptorID
    LEFT JOIN DescriptorCategories dc ON d.descriptorCategory = dc.descriptorCategoryID
    WHERE r.reviewedDrinkID = :drinkId
    ORDER BY r.reviewID, d.descriptorName
""")
    fun getReviewRows(drinkId: Long): Flow<List<ReviewRow>>

    @Update
    suspend fun updateDrink(drink: Drink)
}