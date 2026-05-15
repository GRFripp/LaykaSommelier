package com.example.laykasommelier.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
    (tableName = "Employees")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val employeeID : Long = 0,
    @ColumnInfo("employeeName")
    val employeeName: String,
    @ColumnInfo("employeeEmail")
    val employeeEmail:  String = "123@gmail.com",
    @ColumnInfo("employeePassword")
    val employeePassword: String = "123456",
    @ColumnInfo("employeePosition")
    val employeePosition: String
)
