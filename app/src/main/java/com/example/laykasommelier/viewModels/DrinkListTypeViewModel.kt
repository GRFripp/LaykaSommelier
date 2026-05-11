package com.example.laykasommelier.viewModels

import androidx.lifecycle.ViewModel
import com.example.laykasommelier.data.local.pojo.DrinkListTypes
import com.example.laykasommelier.data.local.repositories.DrinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DrinkListTypeViewModel @Inject constructor(private val repository: DrinkRepository): ViewModel() {


    val drinkListTypes : Flow<List<DrinkListTypes>> = repository.drinkListTypes()


}