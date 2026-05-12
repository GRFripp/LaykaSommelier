package com.example.laykasommelier.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.dao.MakingMethodDao
import com.example.laykasommelier.data.local.entities.Cocktail
import com.example.laykasommelier.data.local.entities.Ingredient
import com.example.laykasommelier.data.local.entities.MakingMethod
import com.example.laykasommelier.data.local.pojo.CocktailIngredientLinkItem
import com.example.laykasommelier.data.local.pojo.editstates.CocktailEditState
import com.example.laykasommelier.data.local.repositories.CocktailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CocktailEditViewModel @Inject constructor(
    private val cocktailRepo: CocktailRepository,
    private val makingMethodRepo: MakingMethodDao,   // предполагаем, что есть
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val cocktailId: Long = savedStateHandle["cocktailId"] ?: -1L

    private val _state = MutableStateFlow(CocktailEditState())
    val state: StateFlow<CocktailEditState> = _state.asStateFlow()

    // Методы приготовления для спиннера
    val makingMethods: StateFlow<List<MakingMethod>> = makingMethodRepo.getAllMakingMethods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Ингредиенты коктейля (связи)
    val ingredientLinks: StateFlow<List<CocktailIngredientLinkItem>> =
        if (cocktailId != -1L) cocktailRepo.getCocktailIngredientsLinks(cocktailId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        else MutableStateFlow(emptyList())

    // Все ингредиенты для диалога выбора
    private val _allIngredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val allIngredients: StateFlow<List<Ingredient>> = _allIngredients.asStateFlow()

    // Канал для сохранения
    private val _saveSuccess = Channel<Unit>(Channel.BUFFERED)
    val saveSuccess: Flow<Unit> = _saveSuccess.receiveAsFlow()

    init {
        viewModelScope.launch {

            _allIngredients.value = cocktailRepo.getAllIngredients().first() // в репозитории getAllIngredients() возвращает Flow -> first()
        }

        if (cocktailId != -1L) {
            viewModelScope.launch {
                val cocktail = cocktailRepo.getCocktail(cocktailId).first()
                _state.value = CocktailEditState(
                    name = cocktail.cocktailName,
                    volume = cocktail.cocktailVolume.toString(),
                    acidity = cocktail.cocktailAcidity.toString(),
                    sugarLevel = cocktail.cocktailSugarLevel.toString(),
                    abv = cocktail.cocktailAbv.toString(),
                    glass = cocktail.cocktailGlass,
                    makingMethodId = cocktail.cocktailMakingMethodID,
                    description = cocktail.cocktailDescription,
                    author = cocktail.cocktailAuthor,
                    serving = cocktail.cocktailServing
                )
            }
        }
    }

    // --- Слушатели изменений полей ---
    fun onNameChanged(name: String) { _state.update { it.copy(name = name) } }
    fun onVolumeChanged(v: String) { _state.update { it.copy(volume = v) } }
    fun onAcidityChanged(v: String) { _state.update { it.copy(acidity = v) } }
    fun onSugarChanged(v: String) { _state.update { it.copy(sugarLevel = v) } }
    fun onAbvChanged(v: String) { _state.update { it.copy(abv = v) } }
    fun onGlassChanged(v: String) { _state.update { it.copy(glass = v) } }
    fun onMakingMethodChanged(id: Long) { _state.update { it.copy(makingMethodId = id) } }
    fun onDescriptionChanged(v: String) { _state.update { it.copy(description = v) } }
    fun onAuthorChanged(v: String) { _state.update { it.copy(author = v) } }
    fun onServingChanged(v: String) { _state.update { it.copy(serving = v) } }

    // Добавить / удалить ингредиент из связи
    fun addIngredient(ingredientId: Long, volume: Double) {
        viewModelScope.launch {
            cocktailRepo.addIngredientLink(cocktailId, ingredientId, volume)
        }
    }

    fun removeIngredient(ingredientId: Long) {
        viewModelScope.launch {
            cocktailRepo.removeIngredientLink(cocktailId, ingredientId)
        }
    }

    // Сохранение коктейля
    fun saveCocktail() {
        viewModelScope.launch {
            val s = _state.value
            if (s.name.isBlank()) return@launch

            val cocktail = Cocktail(
                cocktailID = if (cocktailId == -1L) 0L else cocktailId,
                cocktailName = s.name,
                cocktailVolume = s.volume.toDoubleOrNull() ?: 0.0,
                cocktailAcidity = s.acidity.toDoubleOrNull() ?: 0.0,
                cocktailSugarLevel = s.sugarLevel.toDoubleOrNull() ?: 0.0,
                cocktailAbv = s.abv.toDoubleOrNull() ?: 0.0,
                cocktailGlass = s.glass,
                cocktailMakingMethodID = s.makingMethodId,
                cocktailDescription = s.description,
                cocktailAuthor = s.author,
                cocktailServing = s.serving
            )

            if (cocktailId == -1L) {
                val newId = cocktailRepo.insertCocktail(cocktail)
                // связки ингредиентов уже не сохраняем отдельно – они управляются через addIngredient
            } else {
                cocktailRepo.updateCocktail(cocktail)
            }
            _saveSuccess.send(Unit)
        }
    }
}