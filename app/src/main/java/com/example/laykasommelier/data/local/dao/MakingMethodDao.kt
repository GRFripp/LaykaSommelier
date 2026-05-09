package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laykasommelier.data.local.entities.MakingMethod
import com.example.laykasommelier.data.local.pojo.AdminListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MakingMethodDao {
    @Query("Select * from MakingMethods")
    fun getAllDrinks(): Flow<List<MakingMethod>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(makingMethod: MakingMethod): Long
    @Delete
    suspend fun deleteDrink(makingMethod: MakingMethod)

    @Query("""
        Select makingMethodID as id, makingMethodName as name, makingMethodDilution as dilution
        from MakingMethods
    """)
    fun getALMakingMethods(): Flow<List<AdminListItem.ALMakingMethod>>
}