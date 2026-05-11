package com.example.laykasommelier.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.repositories.DrinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.laykasommelier.data.local.pojo.DrinkDetail
import com.example.laykasommelier.data.local.pojo.DrinkReviewItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DrinkDetailViewModel @Inject constructor(private val drinkRepo: DrinkRepository, savedStateHandle: SavedStateHandle):
    ViewModel() {

    private val drinkId: Long = savedStateHandle["drinkDetailID"]!!

    val drink: Flow<DrinkDetail> = drinkRepo.getDrinkById(drinkId)

    val drinkDetail: StateFlow<DrinkDetail?> = drinkRepo.getDrinkById(drinkId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val reviews: StateFlow<List<DrinkReviewItem>> = drinkRepo.getDrinkReviews(drinkId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}