package com.example.laykasommelier.data.local.pojo.editstates

data class EmployeeEditState(
    val name: String = "",
    val email: String = "123@gmail.com",
    val role: String = "Помощник",   // assistant, bartender, manager
    val password: String = "1234"
)
