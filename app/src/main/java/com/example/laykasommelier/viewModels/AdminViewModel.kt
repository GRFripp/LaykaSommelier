package com.example.laykasommelier.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.pojo.AdminEntityType
import com.example.laykasommelier.data.local.pojo.AdminListItem
import com.example.laykasommelier.data.local.repositories.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(private val adminRepo: AdminRepository): ViewModel()
{
    private val _currentType = MutableStateFlow(AdminEntityType.EMPLOYEES)
    val currentType: StateFlow<AdminEntityType> = _currentType.asStateFlow()

    val items: StateFlow<List<AdminListItem>> = _currentType.flatMapLatest { type ->
        when (type){
            AdminEntityType.DESCRIPTORCATEGORIES -> adminRepo.getDescriptorCategoriesAdmin().map{ list ->
                list.map{ AdminListItem.ALDescriptorCategory(it.id,it.name,it.color) }
            }
            AdminEntityType.EMPLOYEES -> adminRepo.getEmployeesAdmin().map{ list ->
                list.map{ AdminListItem.ALEmployee(it.id,it.name,it.email) }
            }
            AdminEntityType.INGREDIENTS -> adminRepo.getIngredientAdmin().map{list ->
                list.map{ AdminListItem.ALIngredient(it.id,it.name,it.abv) }
            }
            AdminEntityType.DESCRIPTORS -> adminRepo.getDescriptorAdmin().map{ list ->
                list.map{ AdminListItem.ALDescriptor(it.id,it.name,it.category) }
            }
            AdminEntityType.MAKINGMETHODS ->adminRepo.getMakingMethodsAdmin().map(){ list ->
                list.map{ AdminListItem.ALMakingMethod(it.id,it.name,it.dilution) }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),emptyList())

    fun onEntityTypeChanged(type: AdminEntityType){
        _currentType.value = type
    }
}