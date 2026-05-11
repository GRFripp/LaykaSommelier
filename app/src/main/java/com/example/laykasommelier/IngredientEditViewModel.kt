package com.example.laykasommelier

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.entities.DescriptorCategory
import com.example.laykasommelier.data.local.entities.Ingredient
import com.example.laykasommelier.data.local.pojo.DescriptorWithCategory
import com.example.laykasommelier.data.local.pojo.IngredientEditState
import com.example.laykasommelier.data.local.repositories.DescriptorCategoryRepository
import com.example.laykasommelier.data.local.repositories.DescriptorRepository
import com.example.laykasommelier.data.local.repositories.IngredientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class IngredientEditViewModel @Inject constructor(
    private val ingredientRepo: IngredientRepository,
    private val descriptorRepo: DescriptorRepository,
    private val categoryRepo: DescriptorCategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val ingredientId: Long = savedStateHandle["ingredientId"] ?: -1L

    private val _state = MutableStateFlow(IngredientEditState())
    val state: StateFlow<IngredientEditState> = _state.asStateFlow()

    // Полный список категорий (для чипов фильтра)
    private val allCategories = MutableStateFlow<List<DescriptorCategory>>(emptyList())
    val categories: StateFlow<List<DescriptorCategory>> = allCategories.asStateFlow()

    // Отфильтрованный список дескрипторов (с учётом поиска и категории)
    val filteredDescriptors: StateFlow<List<DescriptorWithCategory>> = combine(
        descriptorRepo.getAllDescriptorsWithCategories(),
        _state.map { it.searchQuery },
        _state.map { it.selectedCategoryId }
    ) { descriptors, query, categoryId ->
        descriptors.filter { desc ->
            val matchesQuery = query.isBlank() || desc.descriptorName.contains(query, true)
            val matchesCategory = categoryId == null || desc.categoryId == categoryId
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _saveSuccess = Channel<Unit>(Channel.BUFFERED)
    val saveSuccess: Flow<Unit> = _saveSuccess.receiveAsFlow()

    init {
        // Загружаем все категории
        viewModelScope.launch {
            allCategories.value = categoryRepo.getAllCategories()
        }

        // Если редактирование – загружаем ингредиент
        if (ingredientId != -1L) {
            viewModelScope.launch {
                val ingredient = ingredientRepo.getById(ingredientId)
                val descriptorIds = ingredientRepo.getDescriptorIdsForIngredient(ingredientId)
                _state.value = IngredientEditState(
                    name = ingredient.ingredientName,
                    acidity = ingredient.ingredientAcidity.toString(),
                    sugarLevel = ingredient.ingredientSugarLevel.toString(),
                    selectedDescriptorIds = descriptorIds
                )
            }
        }
    }

    fun onNameChanged(name: String) { _state.update { it.copy(name = name) } }
    fun onAcidityChanged(acidity: String) { _state.update { it.copy(acidity = acidity) } }
    fun onSugarChanged(sugar: String) { _state.update { it.copy(sugarLevel = sugar) } }
    fun onSearchChanged(query: String) { _state.update { it.copy(searchQuery = query) } }
    fun onCategoryFilterSelected(categoryId: Long?) {
        _state.update { it.copy(selectedCategoryId = if (it.selectedCategoryId == categoryId) null else categoryId) }
    }
    fun onDescriptorToggled(descriptorId: Long) {
        _state.update { state ->
            val newSet = state.selectedDescriptorIds.toMutableSet()
            if (newSet.contains(descriptorId)) newSet.remove(descriptorId) else newSet.add(descriptorId)
            state.copy(selectedDescriptorIds = newSet)
        }
    }

    fun save() {
        viewModelScope.launch {
            val st = _state.value
            if (st.name.isBlank()) return@launch

            val ingredient = Ingredient(
                ingredientID = if (ingredientId == -1L) 0L else ingredientId,
                ingredientName = st.name,
                ingredientAcidity = st.acidity.toDoubleOrNull() ?: 0.0,
                ingredientSugarLevel = st.sugarLevel.toDoubleOrNull() ?: 0.0
            )
            val newId = if (ingredientId == -1L) {
                ingredientRepo.insertIngredient(ingredient)
            } else {
                ingredientRepo.updateIngredient(ingredient)
                ingredientId
            }
            // Обновить связь с дескрипторами
            ingredientRepo.updateIngredientDescriptors(newId, st.selectedDescriptorIds.toList())
            _saveSuccess.send(Unit)
            Log.d("IngredientEdit", "Сохраняю ингредиент, name=${st.name}, descriptorCount=${st.selectedDescriptorIds.size}")
        }
    }
}