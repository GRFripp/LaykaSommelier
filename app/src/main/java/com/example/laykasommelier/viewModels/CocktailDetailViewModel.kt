package com.example.laykasommelier.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.entities.Cocktail
import com.example.laykasommelier.data.local.pojo.CocktailIngredientItem
import com.example.laykasommelier.data.local.repositories.CocktailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CocktailDetailViewModel@Inject constructor(
    private val repository: CocktailRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val cocktailId: Long = savedStateHandle["cocktailDetailId"] ?: -1L

    val cocktail: StateFlow<Cocktail?> = repository.getCocktail(cocktailId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Название метода приготовления
    val makingMethodName: StateFlow<String> = cocktail.flatMapLatest { cocktail ->
        if (cocktail != null) {
            repository.getMakingMethod(cocktail.cocktailMakingMethodID)
                .map { it?.makingMethodName ?: "Не указан" }
        } else {
            flowOf("")
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val ingredients: StateFlow<List<CocktailIngredientItem>> =
        repository.getCocktailIngredients(cocktailId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}