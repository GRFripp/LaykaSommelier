package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laykasommelier.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DescriptorDao {
   /* @Query("""
      SELECT descriptorName, descriptorCategoryName
       FROM Descriptors
        INNER JOIN DescriptorCategories ON descriptorCategory = descriptorCategoryID
    """)
    fun getAllDescriptors(): Flow<List<Drink>>*/
    @Query("Select * from Descriptors")
    fun getAllDescriptors(): Flow<List<Descriptor>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDescriptor(descriptor: Descriptor): Long
    @Delete
    suspend fun deleteDescriptor(descriptor: Descriptor)

}