package com.example.laykasommelier.viewModels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.entities.Drink
import com.example.laykasommelier.data.local.pojo.DrinkReviewItem
import com.example.laykasommelier.data.local.pojo.editstates.DrinkEditState
import com.example.laykasommelier.data.local.repositories.DrinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrinkEditViewModel @Inject constructor(
    private val drinkRepo: DrinkRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var drinkId: Long = savedStateHandle["drinkId"] ?: -1L

    private val _state = MutableStateFlow(DrinkEditState())
    val state: StateFlow<DrinkEditState> = _state.asStateFlow()

    // Поток рецензий: переключается, когда drinkId становится известен (после сохранения нового напитка)
    private val _drinkIdFlow = MutableStateFlow(drinkId)

    val reviews: StateFlow<List<DrinkReviewItem>> = _drinkIdFlow.flatMapLatest { id ->
        if (id != -1L) drinkRepo.getDrinkReviews(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _saveSuccess = Channel<Unit>(Channel.BUFFERED)
    val saveSuccess: Flow<Unit> = _saveSuccess.receiveAsFlow()

    init {
        if (drinkId != -1L) {
            loadDrink(drinkId)
        }
    }

    private fun loadDrink(id: Long) {
        viewModelScope.launch {
            val drink = drinkRepo.getDrinkById(id).first() // suspend функция, возвращает Drink
            Log.d("CHECK DRINK EDIT","drink: ${drink.name}")
            _state.value = DrinkEditState(
                name = drink.name,
                type = drink.type,
                subType = drink.subType ?: "",
                country = drink.country ?: "",
                producer = drink.producer ?: "",
                aged = drink.aged?.toString() ?: "",
                abv = drink.abv.toString()
            )
        }
    }

    fun onNameChanged(name: String) { _state.update { it.copy(name = name) } }
    fun onTypeChanged(type: String) { _state.update { it.copy(type = type) } }
    fun onSubTypeChanged(subType: String) { _state.update { it.copy(subType = subType) } }
    fun onCountryChanged(country: String) { _state.update { it.copy(country = country) } }
    fun onProducerChanged(producer: String) { _state.update { it.copy(producer = producer) } }
    fun onAgedChanged(aged: String) { _state.update { it.copy(aged = aged) } }
    fun onAbvChanged(abv: String) { _state.update { it.copy(abv = abv) } }

    fun saveDrink() {
        viewModelScope.launch {
            val s = _state.value
            if (s.name.isBlank()) return@launch
            val drink = Drink(
                drinkID = if (drinkId == -1L) 0L else drinkId,
                drinkName = s.name,
                drinkType = s.type,
                drinkSubType = s.subType.ifBlank { null },
                drinkCountry = s.country.ifBlank { null },
                drinkProducer = s.producer.ifBlank { null },
                drinkAged = s.aged.toIntOrNull() ?: 0,
                drinkAbv = s.abv.toDoubleOrNull() ?: 0.0,
                drinkImageUrl = "null"
            )
            val newId = if (drinkId == -1L) {
                drinkRepo.insertDrink(drink)
            } else {
                drinkRepo.updateDrink(drink)
                drinkId
            }
            if (drinkId == -1L) {
                drinkId = newId
                _drinkIdFlow.value = newId
            }
            _saveSuccess.send(Unit)
        }
    }

    fun refreshReviews() {
        _drinkIdFlow.value = drinkId // триггерит перезагрузку
    }
}