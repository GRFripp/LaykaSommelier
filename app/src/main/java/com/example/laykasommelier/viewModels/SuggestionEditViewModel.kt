package com.example.laykasommelier.viewModels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.SessionManager
import com.example.laykasommelier.data.local.dao.EmployeeDao
import com.example.laykasommelier.data.local.dao.MakingMethodDao
import com.example.laykasommelier.data.local.entities.Cocktail
import com.example.laykasommelier.data.local.entities.MakingMethod
import com.example.laykasommelier.data.local.pojo.EmployeeRole
import com.example.laykasommelier.data.local.pojo.editstates.SuggestionEditState
import com.example.laykasommelier.data.local.repositories.CocktailRepository
import com.example.laykasommelier.data.local.repositories.SuggestionRepository
import com.example.laykasommelier.network.ApiService
import com.example.laykasommelier.network.dto.CocktailCreateRequest
import com.example.laykasommelier.network.dto.SuggestionStatusUpdateRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
class SuggestionEditViewModel @Inject constructor(
    private val suggestionRepo: SuggestionRepository,
    private val cocktailRepo: CocktailRepository,
    private val makingMethodRepo: MakingMethodDao,
    private val employeeDao: EmployeeDao,
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    @ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val suggestionId: Long = savedStateHandle["suggestionId"] ?: -1L
    private var currentCocktailId: Long = -1L
    private val _state = MutableStateFlow(SuggestionEditState())
    val state: StateFlow<SuggestionEditState> = _state.asStateFlow()

    val makingMethods: StateFlow<List<MakingMethod>> = makingMethodRepo.getAllMakingMethods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _saveSuccess = Channel<Unit>(Channel.BUFFERED)
    val saveSuccess: Flow<Unit> = _saveSuccess.receiveAsFlow()

    init {
        if (suggestionId != -1L) {
            viewModelScope.launch {
                val suggestion = suggestionRepo.getSuggestionById(suggestionId).first() ?: return@launch
                val cocktail = cocktailRepo.getCocktail(suggestion.suggestedCocktailID).first()
                val emp = employeeDao.getById(suggestion.suggestionEmployeeID)
                val empName = emp?.employeeName ?: "Неизвестный"

                _state.value = SuggestionEditState(
                    name = cocktail.cocktailName,
                    volume = cocktail.cocktailVolume.toString(),
                    acidity = cocktail.cocktailAcidity.toString(),
                    sugarLevel = cocktail.cocktailSugarLevel.toString(),
                    abv = cocktail.cocktailAbv.toString(),
                    glass = cocktail.cocktailGlass,
                    makingMethodId = cocktail.cocktailMakingMethodID,
                    description = cocktail.cocktailDescription,
                    author = cocktail.cocktailAuthor,
                    serving = cocktail.cocktailServing,
                    employeeName = empName,
                    suggestionStatus = suggestion.suggestionStatus,
                    imageUrl = cocktail.cocktailImageUrl
                )
            }
        }
    }


    // --- методы изменения полей ---
    fun onNameChanged(v: String) { _state.update { it.copy(name = v) } }
    fun onVolumeChanged(v: String) { _state.update { it.copy(volume = v) } }
    fun onAcidityChanged(v: String) { _state.update { it.copy(acidity = v) } }
    fun onSugarChanged(v: String) { _state.update { it.copy(sugarLevel = v) } }
    fun onAbvChanged(v: String) { _state.update { it.copy(abv = v) } }
    fun onGlassChanged(v: String) { _state.update { it.copy(glass = v) } }
    fun onMakingMethodChanged(id: Long) { _state.update { it.copy(makingMethodId = id) } }
    fun onDescriptionChanged(v: String) { _state.update { it.copy(description = v) } }
    fun onAuthorChanged(v: String) { _state.update { it.copy(author = v) } }
    fun onServingChanged(v: String) { _state.update { it.copy(serving = v) } }

    // Статус можно менять через отдельные кнопки, но можно и через выпадающий список
    fun setStatus(status: String) {
        // Только менеджер может менять статус
        if (sessionManager.getRole() != EmployeeRole.MANAGER) return
        _state.update { it.copy(suggestionStatus = status) }
    }

    // Сохранение (обновление коктейля и статуса предложения)
    fun save() {
        viewModelScope.launch {
            val st = _state.value
            if (st.name.isBlank()) return@launch

            // 1. Обновить коктейль на сервере
            val request = CocktailCreateRequest(
                name = st.name,
                volume = st.volume.toDoubleOrNull() ?: 0.0,
                acidity = st.acidity.toDoubleOrNull() ?: 0.0,
                sugarLevel = st.sugarLevel.toDoubleOrNull() ?: 0.0,
                abv = st.abv.toDoubleOrNull() ?: 0.0,
                glass = st.glass,
                makingMethodId = st.makingMethodId,
                description = st.description,
                author = st.author,
                serving = st.serving,
                imageUrl = st.imageUrl
            )
            apiService.updateCocktail(currentCocktailId, request)

            // 2. Обновить локальный коктейль в Room
            val updatedCocktail = Cocktail(
                cocktailID = currentCocktailId,
                cocktailName = st.name,
                cocktailVolume = st.volume.toDoubleOrNull() ?: 0.0,
                cocktailAcidity = st.acidity.toDoubleOrNull() ?: 0.0,
                cocktailSugarLevel = st.sugarLevel.toDoubleOrNull() ?: 0.0,
                cocktailAbv = st.abv.toDoubleOrNull() ?: 0.0,
                cocktailGlass = st.glass,
                cocktailMakingMethodID = st.makingMethodId,
                cocktailDescription = st.description,
                cocktailAuthor = st.author,
                cocktailServing = st.serving,
                cocktailImageUrl = st.imageUrl
            )
            cocktailRepo.updateCocktail(updatedCocktail)

            // 3. Статус заявки меняем только если менеджер
            if (sessionManager.getRole() == EmployeeRole.MANAGER) {
                val statusRequest = SuggestionStatusUpdateRequest(st.suggestionStatus)
                apiService.updateSuggestionStatus(suggestionId, statusRequest)
                suggestionRepo.updateSuggestionStatus(suggestionId, st.suggestionStatus)
            }

            _saveSuccess.send(Unit)
        }
    }

    fun approve() {
        if (sessionManager.getRole() != EmployeeRole.MANAGER) return
        viewModelScope.launch {
            val statusRequest = SuggestionStatusUpdateRequest("approved")
            apiService.updateSuggestionStatus(suggestionId, statusRequest)
            suggestionRepo.updateSuggestionStatus(suggestionId, "approved")
            _state.update { it.copy(suggestionStatus = "approved") }
            _saveSuccess.send(Unit)
        }
    }

    fun reject() {
        if (sessionManager.getRole() != EmployeeRole.MANAGER) return
        viewModelScope.launch {
            val statusRequest = SuggestionStatusUpdateRequest("rejected")
            apiService.updateSuggestionStatus(suggestionId, statusRequest)
            suggestionRepo.updateSuggestionStatus(suggestionId, "rejected")
            _state.update { it.copy(suggestionStatus = "rejected") }
            _saveSuccess.send(Unit)
        }
    }
}