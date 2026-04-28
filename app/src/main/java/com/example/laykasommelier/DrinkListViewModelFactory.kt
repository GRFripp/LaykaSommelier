package com.example.laykasommelier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.laykasommelier.data.local.repositories.DrinkRepository

class DrinkListViewModelFactory(private val repository: DrinkRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DrinkListViewModel(repository) as T
    }
}