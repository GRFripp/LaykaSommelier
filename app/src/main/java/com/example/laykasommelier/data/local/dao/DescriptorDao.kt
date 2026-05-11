package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.laykasommelier.data.local.entities.*
import com.example.laykasommelier.data.local.pojo.AdminListItem
import com.example.laykasommelier.data.local.pojo.DescriptorWithCategory
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

    @Query("""
        Select descriptorID as id, descriptorName as name, descriptorCategoryName as category
        from
        Descriptors as d inner join DescriptorCategories as dc on d.descriptorCategory = dc.descriptorCategoryID;
    """)
    fun getALDescriptors(): Flow<List<AdminListItem.ALDescriptor>>

    @Query("""
        Select descriptorID as id, descriptorName as name, descriptorCategoryName as category
        from
        Descriptors as d inner join DescriptorCategories as dc on d.descriptorCategory = dc.descriptorCategoryID
        where id = :id;
    """)
    fun getDescriptorById(id: Long):Flow<AdminListItem.ALDescriptor>

    @Query("""
    SELECT 
        d.descriptorID AS descriptorId,
        d.descriptorName AS descriptorName,
        c.descriptorCategoryID AS categoryId,
        c.descriptorCategoryName AS categoryName,
        c.descriptorCategoryColor AS categoryColor
    FROM Descriptors d
    INNER JOIN DescriptorCategories c ON d.descriptorCategory = c.descriptorCategoryID
    ORDER BY c.descriptorCategoryName, d.descriptorName
""")
    fun getAllDescriptorsWithCategoryFlow(): Flow<List<DescriptorWithCategory>>
    @Query("SELECT * FROM Descriptors WHERE descriptorID = :id")
    suspend fun getEditDescriptorById(id: Long): Descriptor?

    @Update
    suspend fun updateDescriptor(descriptor: Descriptor)
}