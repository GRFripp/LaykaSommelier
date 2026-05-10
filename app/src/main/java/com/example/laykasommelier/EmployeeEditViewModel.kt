package com.example.laykasommelier

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.entities.Employee
import com.example.laykasommelier.data.local.pojo.EmployeeEditState
import com.example.laykasommelier.data.local.repositories.EmployeeRepository
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
                    role = emp.employeePosition,
                    password = "" // пароль не храним в открытом виде, оставляем пустым
                )
            }
        }
    }

    fun onNameChanged(name: String) { _state.update { it.copy(name = name) } }
    fun onRoleChanged(role: String) { _state.update { it.copy(role = role) } }
    fun onPasswordChanged(password: String) { _state.update { it.copy(password = password) } }

    fun save() {
        viewModelScope.launch {
            val st = _state.value
            if (st.name.isBlank()) {
                // можно передать ошибку через отдельный StateFlow
                return@launch
            }
            val employee = Employee(
                employeeID = if (employeeId == -1L) 0L else employeeId,
                employeeName = st.name,
                employeePassword = st.password, // здесь нужно хэширование, пока для простоты так
                employeePosition = st.role
            )
            if (employeeId == -1L) {
                employeeRepo.insertEmployee(employee)
            } else {
                employeeRepo.updateEmployee(employee)
            }
            _saveSuccess.send(Unit)
        }
    }
}