package com.example.laykasommelier

import androidx.lifecycle.ViewModel
import com.example.laykasommelier.data.local.pojo.DrinkListTypes
import com.example.laykasommelier.data.local.repositories.DrinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DrinkListViewModel @Inject constructor(private val repository: DrinkRepository): ViewModel() {
    val drinkListTypes : Flow<List<DrinkListTypes>> = repository.drinkListTypes()
}