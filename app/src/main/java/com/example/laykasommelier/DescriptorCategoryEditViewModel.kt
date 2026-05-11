package com.example.laykasommelier

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.entities.DescriptorCategory
import com.example.laykasommelier.data.local.pojo.DescriptorCategoryEditState
import com.example.laykasommelier.data.local.repositories.DescriptorCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DescriptorCategoryEditViewModel @Inject constructor(
    private val categoryRepo: DescriptorCategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val categoryId: Long = savedStateHandle["categoryId"] ?: -1L

    private val _state = MutableStateFlow(DescriptorCategoryEditState())
    val state: StateFlow<DescriptorCategoryEditState> = _state.asStateFlow()

    private val _saveSuccess = Channel<Unit>(Channel.BUFFERED)
    val saveSuccess: Flow<Unit> = _saveSuccess.receiveAsFlow()

    init {
        if (categoryId != -1L) {
            viewModelScope.launch {
                val category = categoryRepo.getById(categoryId)
                _state.value = DescriptorCategoryEditState(
                    name = category.descriptorCategoryName,
                    color = category.descriptorCategoryColor
                )
            }
        }
    }

    fun onNameChanged(name: String) { _state.update { it.copy(name = name) } }
    fun onColorChanged(color: String) { _state.update { it.copy(color = color) } }

    fun save() {
        viewModelScope.launch {
            val st = _state.value
            if (st.name.isBlank()) return@launch

            val category = DescriptorCategory(
                descriptorCategoryID = if (categoryId == -1L) 0L else categoryId,
                descriptorCategoryName = st.name,
                descriptorCategoryColor = st.color
            )
            if (categoryId == -1L) {
                categoryRepo.insert(category)
            } else {
                categoryRepo.update(category)
            }
            _saveSuccess.send(Unit)
        }
    }
}