package com.example.laykasommelier.data.local.pojo

sealed class AdminListItem {
    data class ALDescriptor(val id: Long, val name: String, val category: String): AdminListItem()
    data class ALIngredient(val id: Long,val name: String, val abv: String): AdminListItem()
    data class ALEmployee(val id: Long,val name: String, val email: String): AdminListItem()
    data class ALDescriptorCategory(val id: Long,val name: String, val color: String): AdminListItem()
    data class ALMakingMethod(val id: Long,val name: String, val dilution: Double): AdminListItem()
}