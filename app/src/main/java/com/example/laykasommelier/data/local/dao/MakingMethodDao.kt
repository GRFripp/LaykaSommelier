package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.laykasommelier.data.local.entities.MakingMethod
import com.example.laykasommelier.data.local.pojo.AdminListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MakingMethodDao {
    @Query("Select * from MakingMethods")
    fun getAllMakingMethods(): Flow<List<MakingMethod>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMakingMethods(makingMethod: MakingMethod): Long
    @Delete
    suspend fun deleteMakingMethods(makingMethod: MakingMethod)

    @Query("""
        Select makingMethodID as id, makingMethodName as name, makingMethodDilution as dilution
        from MakingMethods
    """)
    fun getALMakingMethods(): Flow<List<AdminListItem.ALMakingMethod>>

    @Query("""
        Select makingMethodID as id, makingMethodName as name, makingMethodDilution as dilution
        from MakingMethods
        where id = :id
    """)
    fun getMakingMethodById(id: Long): Flow<AdminListItem.ALMakingMethod>

    @Query("SELECT * FROM MakingMethods WHERE makingMethodID = :id")
    suspend fun getMethodById(id: Long): MakingMethod?

    @Update
    suspend fun updateMethod(method: MakingMethod)
}