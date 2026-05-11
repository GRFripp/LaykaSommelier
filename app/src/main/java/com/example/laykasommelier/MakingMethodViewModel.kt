package com.example.laykasommelier

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.entities.MakingMethod
import com.example.laykasommelier.data.local.pojo.MakingMethodEditState
import com.example.laykasommelier.data.local.repositories.MakingMethodRepository
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
class MakingMethodViewModel @Inject constructor(
    private val methodRepo: MakingMethodRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(){
    private val methodId: Long = savedStateHandle["methodId"] ?: -1L

    private val _state = MutableStateFlow(MakingMethodEditState())
    val state: StateFlow<MakingMethodEditState> = _state.asStateFlow()

    private val _saveSuccess = Channel<Unit>(Channel.BUFFERED)
    val saveSuccess: Flow<Unit> = _saveSuccess.receiveAsFlow()

    init {
        if (methodId != -1L) {
            viewModelScope.launch {
                val method = methodRepo.getById(methodId)
                _state.value = MakingMethodEditState(
                    name = method.makingMethodName,
                    dilution = method.makingMethodDilution.toString()
                )
            }
        }
    }

    fun onNameChanged(name: String) { _state.update { it.copy(name = name) } }
    fun onDilutionChanged(dilution: String) { _state.update { it.copy(dilution = dilution) } }

    fun save() {
        viewModelScope.launch {
            val st = _state.value
            if (st.name.isBlank()) return@launch

            val method = MakingMethod(
                makingMethodID = if (methodId == -1L) 0L else methodId,
                makingMethodName = st.name,
                makingMethodDilution = st.dilution.toDoubleOrNull() ?: 0.0
            )
            if (methodId == -1L) {
                methodRepo.insert(method)
            } else {
                methodRepo.update(method)
            }
            _saveSuccess.send(Unit)
        }
    }
}