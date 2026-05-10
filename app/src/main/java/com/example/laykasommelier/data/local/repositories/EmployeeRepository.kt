package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.dao.EmployeeDao
import com.example.laykasommelier.data.local.entities.Employee

class EmployeeRepository(val employeeDao: EmployeeDao) {
    suspend fun getEmployeeById(id: Long): Employee = employeeDao.getById(id)
        ?: throw Exception("Сотрудник не найден")

    suspend fun insertEmployee(employee: Employee): Long = employeeDao.insert(employee)

    suspend fun updateEmployee(employee: Employee) = employeeDao.update(employee)
}