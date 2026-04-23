package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laykasommelier.data.local.entities.Employee
import kotlinx.coroutines.flow.Flow
@Dao
interface EmployeeDao {
    @Query("Select * from Employees")
    fun getAllDrinks(): Flow<List<Employee>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(employee: Employee): Long
    @Delete
    suspend fun deleteDrink(employee: Employee)
}