package com.example.laykasommelier

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.laykasommelier.data.local.repositories.DrinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.laykasommelier.data.local.entities.Drink
import com.example.laykasommelier.data.local.pojo.DrinkDetail
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class DrinkDetailViewModel @Inject constructor(private val drinkRepo: DrinkRepository, savedStateHandle: SavedStateHandle):
    ViewModel() {

    private val drinkId: Long = savedStateHandle["drinkDetailID"]!!

    val drink: Flow<DrinkDetail> = drinkRepo.getDrinkById(drinkId)

}