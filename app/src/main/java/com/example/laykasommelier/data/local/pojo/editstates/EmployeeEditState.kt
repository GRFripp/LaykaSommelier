package com.example.laykasommelier.data.local.pojo.editstates

data class EmployeeEditState(
    val name: String = "",
    val role: String = "assistant",   // assistant, bartender, manager
    val password: String = ""
)
