package com.example.laykasommelier.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.pojo.CocktailListPreviews
import com.example.laykasommelier.data.local.repositories.CocktailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CocktailListViewModel @Inject constructor(private val cocktailRepository: CocktailRepository): ViewModel()
{
    val cocktailPreviews: StateFlow<List<CocktailListPreviews>> = cocktailRepository.getCocktailPreviews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}