package com.example.laykasommelier

import androidx.lifecycle.ViewModel
import com.example.laykasommelier.data.local.pojo.DrinkListTypes
import com.example.laykasommelier.data.local.repositories.DrinkRepository
import kotlinx.coroutines.flow.Flow

class DrinkListViewModel(private val repository: DrinkRepository): ViewModel() {
    val drinkListTypes : Flow<List<DrinkListTypes>> = repository.drinkListTypes()
}