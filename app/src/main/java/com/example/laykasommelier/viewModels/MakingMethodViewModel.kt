package com.example.laykasommelier.viewModels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.entities.MakingMethod
import com.example.laykasommelier.data.local.pojo.editstates.MakingMethodEditState
import com.example.laykasommelier.data.local.repositories.MakingMethodRepository
import com.example.laykasommelier.network.ApiService
import com.example.laykasommelier.network.dto.MakingMethodCreateRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MakingMethodViewModel @Inject constructor(
    private val methodRepo: MakingMethodRepository,
    private val apiService: ApiService,
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
                val method = methodRepo.getById(methodId).first()
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

            val request = MakingMethodCreateRequest(
                name = st.name,
                dilution = st.dilution.toDoubleOrNull() ?: 0.0
            )

            try {
                if (methodId == -1L) {
                    val created = apiService.createMakingMethod(request)
                    methodRepo.insert(
                        MakingMethod(
                            makingMethodID = created.id,
                            makingMethodName = created.name,
                            makingMethodDilution = created.dilution
                        )
                    )
                } else {
                    apiService.updateMakingMethod(methodId, request)
                    methodRepo.update(
                        MakingMethod(
                            makingMethodID = methodId,
                            makingMethodName = st.name,
                            makingMethodDilution = st.dilution.toDoubleOrNull() ?: 0.0
                        )
                    )
                }
                _saveSuccess.send(Unit)
            } catch (e: Exception) {
                Log.e("MakingMethodEdit", "Server save failed", e)
            }
        }
    }
}