package com.example.laykasommelier

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.pojo.DrinkListPreviews
import com.example.laykasommelier.data.local.pojo.DrinkListTypes
import com.example.laykasommelier.data.local.repositories.DrinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DrinkListTypeViewModel @Inject constructor(private val repository: DrinkRepository): ViewModel() {


    val drinkListTypes : Flow<List<DrinkListTypes>> = repository.drinkListTypes()


}