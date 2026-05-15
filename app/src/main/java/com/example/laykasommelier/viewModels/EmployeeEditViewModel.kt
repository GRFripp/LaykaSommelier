package com.example.laykasommelier.viewModels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.entities.Employee
import com.example.laykasommelier.data.local.pojo.editstates.EmployeeEditState
import com.example.laykasommelier.data.local.repositories.EmployeeRepository
import com.example.laykasommelier.network.ApiService
import com.example.laykasommelier.network.dto.EmployeeCreateRequest
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
class EmployeeEditViewModel @Inject constructor(
    private val employeeRepo: EmployeeRepository,
    private val apiService: ApiService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val employeeId: Long = savedStateHandle["employeeId"] ?: -1L

    private val _state = MutableStateFlow(EmployeeEditState())
    val state: StateFlow<EmployeeEditState> = _state.asStateFlow()

    // События (например, для закрытия диалога)
    private val _saveSuccess = Channel<Unit>(Channel.BUFFERED)
    val saveSuccess: Flow<Unit> = _saveSuccess.receiveAsFlow()

    init {
        if (employeeId != -1L) {
            viewModelScope.launch {
                val emp = employeeRepo.getEmployeeById(employeeId)
                _state.value = EmployeeEditState(
                    name = emp.employeeName,
                    email = emp.employeeEmail,
                    role = emp.employeePosition,
                    password = emp.employeePassword   // пароль не заполняем при редактировании
                )
            }
        }
    }

    fun onNameChanged(name: String) { _state.update { it.copy(name = name) } }
    fun onRoleChanged(role: String) { _state.update { it.copy(role = role) } }
    fun onPasswordChanged(password: String) { _state.update { it.copy(password = password) } }
    fun onEmailChanged(email: String) { _state.update { it.copy(email = email) } }
    fun save() {
        viewModelScope.launch {
            val st = _state.value
            if (st.name.isBlank()) return@launch

            val request = EmployeeCreateRequest(
                name = st.name,
                email = st.email.ifBlank { "123@gmail.com" },
                password = st.password.ifBlank { "1234" },
                position = st.role
            )

            try {
                if (employeeId == -1L) {
                    val created = apiService.createEmployee(request)
                    // Сохраняем локально с присвоенным ID
                    employeeRepo.insertEmployee(
                        Employee(
                            employeeID = created.id,
                            employeeName = created.name,
                            employeePassword = st.password.ifBlank { "1234" },
                            employeePosition = created.position
                        )
                    )
                } else {
                    apiService.updateEmployee(employeeId, request)
                    employeeRepo.updateEmployee(
                        Employee(
                            employeeID = employeeId,
                            employeeName = st.name,
                            employeePassword = st.password.ifBlank { "1234" },
                            employeePosition = st.role
                        )
                    )
                }
                _saveSuccess.send(Unit)
            } catch (e: Exception) {
                Log.e("EmployeeEdit", "Server save failed", e)
            }
        }
    }
}