package com.example.laykasommelier.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.SessionManager
import com.example.laykasommelier.data.local.mapper.toEntity
import com.example.laykasommelier.data.local.pojo.SuggestionPreviewItem
import com.example.laykasommelier.data.local.repositories.SuggestionRepository
import com.example.laykasommelier.network.ApiService
import com.example.laykasommelier.network.dto.SuggestionDto
import com.example.laykasommelier.network.dto.SuggestionStatusUpdateRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuggestionListViewModel @Inject constructor(
    private val apiService: ApiService,
    private val suggestionRepo: SuggestionRepository,
    private var sessionManager: SessionManager
) : ViewModel() {
    private val _suggestions = MutableStateFlow<List<SuggestionDto>>(emptyList())
    val suggestions: StateFlow<List<SuggestionDto>> = _suggestions.asStateFlow()

    init {
        loadSuggestions()
    }

    fun loadSuggestions() {
        viewModelScope.launch {
            try {
                val all = apiService.getSuggestions()
                _suggestions.value = all
                // Сохраняем в Room для обновления локального списка
                suggestionRepo.insertAll(all.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e("SuggestionList", "Failed to load suggestions", e)
            }
        }
    }

    fun approveSuggestion(suggestionId: Long) {
        viewModelScope.launch {
            try {
                apiService.updateSuggestionStatus(suggestionId, SuggestionStatusUpdateRequest("approved"))
                loadSuggestions() // обновить список
            } catch (e: Exception) {
                Log.e("SuggestionList", "Approve failed", e)
            }
        }
    }

    fun rejectSuggestion(suggestionId: Long) {
        viewModelScope.launch {
            try {
                apiService.updateSuggestionStatus(suggestionId, SuggestionStatusUpdateRequest("rejected"))
                loadSuggestions()
            } catch (e: Exception) {
                Log.e("SuggestionList", "Reject failed", e)
            }
        }
    }
    val suggestionPreviews: StateFlow<List<SuggestionPreviewItem>> =
        suggestionRepo.getSuggestionPreviews()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}