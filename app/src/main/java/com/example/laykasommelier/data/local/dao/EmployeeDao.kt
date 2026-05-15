package com.example.laykasommelier.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.laykasommelier.data.local.entities.Employee
import com.example.laykasommelier.data.local.pojo.AdminListItem
import kotlinx.coroutines.flow.Flow
@Dao
interface EmployeeDao {
    @Query("Select * from Employees")
    fun getAllDrinks(): Flow<List<Employee>>

    @Delete
    suspend fun deleteDrink(employee: Employee)

    @Query("""
        Select employeeID as id, employeeName as name, employeeEmail as email
        From Employees
    """)
    fun getALEmployees(): Flow<List<AdminListItem.ALEmployee>>

    @Query("SELECT * FROM Employees WHERE employeeID = :id")
    suspend fun getById(id: Long): Employee?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(employee: Employee): Long

    @Update
    suspend fun update(employee: Employee)

    @Query("DELETE FROM Employees")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Employee>)
}