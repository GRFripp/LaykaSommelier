package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laykasommelier.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DescriptorCategoryDao {
    @Query("Select * from DescriptorCategories")
    fun getAllDescriptorCategories(): Flow<List<DescriptorCategory>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDescriptorCategory(descriptorCategory: DescriptorCategory): Long
    @Delete
    suspend fun deleteDescriptorCategory(descriptorCategory: DescriptorCategory)
}