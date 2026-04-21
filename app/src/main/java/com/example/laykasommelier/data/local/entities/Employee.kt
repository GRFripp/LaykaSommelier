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
    @ColumnInfo("employeePassword")
    val employeePassword: String = "1234",
    @ColumnInfo("employeePosition")
    val employeePosition: String
)
