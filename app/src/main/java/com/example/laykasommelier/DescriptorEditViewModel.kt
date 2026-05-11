package com.example.laykasommelier

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.entities.Descriptor
import com.example.laykasommelier.data.local.entities.DescriptorCategory
import com.example.laykasommelier.data.local.pojo.DescriptorEditState
import com.example.laykasommelier.data.local.repositories.DescriptorCategoryRepository
import com.example.laykasommelier.data.local.repositories.DescriptorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DescriptorEditViewModel @Inject constructor(
    private val descriptorRepo: DescriptorRepository,
    private val categoryRepo: DescriptorCategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val descriptorId: Long = savedStateHandle["descriptorId"] ?: -1L

    private val _state = MutableStateFlow(DescriptorEditState())
    val state: StateFlow<DescriptorEditState> = _state.asStateFlow()

    private val _categories = MutableStateFlow<List<DescriptorCategory>>(emptyList())
    val categories: StateFlow<List<DescriptorCategory>> = _categories.asStateFlow()

    private val _saveSuccess = Channel<Unit>(Channel.BUFFERED)
    val saveSuccess: Flow<Unit> = _saveSuccess.receiveAsFlow()

    init {
        viewModelScope.launch {
            _categories.value = categoryRepo.getAllCategories()
        }

        if (descriptorId != -1L) {
            viewModelScope.launch {
                val descriptor = descriptorRepo.getById(descriptorId)
                _state.value = DescriptorEditState(
                    name = descriptor.descriptorName,
                    categoryId = descriptor.descriptorCategory
                )
            }
        }
    }

    fun onNameChanged(name: String) { _state.update { it.copy(name = name) } }
    fun onCategorySelected(categoryId: Long) { _state.update { it.copy(categoryId = categoryId) } }

    fun save() {
        viewModelScope.launch {
            val st = _state.value
            if (st.name.isBlank() || st.categoryId == -1L) return@launch

            val descriptor = Descriptor(
                descriptorID = if (descriptorId == -1L) 0L else descriptorId,
                descriptorName = st.name,
                descriptorCategory = st.categoryId
            )
            if (descriptorId == -1L) {
                descriptorRepo.insert(descriptor)
            } else {
                descriptorRepo.update(descriptor)
            }
            _saveSuccess.send(Unit)
        }
    }
}