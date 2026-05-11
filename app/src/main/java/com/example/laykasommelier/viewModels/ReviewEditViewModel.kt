package com.example.laykasommelier.viewModels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.entities.DescriptorCategory
import com.example.laykasommelier.data.local.entities.Review
import com.example.laykasommelier.data.local.entities.Source
import com.example.laykasommelier.data.local.pojo.DescriptorWithCategory
import com.example.laykasommelier.data.local.pojo.editstates.ReviewEditState
import com.example.laykasommelier.data.local.repositories.DescriptorCategoryRepository
import com.example.laykasommelier.data.local.repositories.DescriptorRepository
import com.example.laykasommelier.data.local.repositories.ReviewRepository
import com.example.laykasommelier.data.local.repositories.SourceRepository
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
class ReviewEditViewModel @Inject constructor(
    private val reviewRepo: ReviewRepository,
    private val sourceRepo: SourceRepository,
    private val descriptorRepo: DescriptorRepository,
    private val categoryRepo: DescriptorCategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val drinkId: Long = savedStateHandle["drinkId"]!!
     val reviewId: Long = savedStateHandle["reviewId"] ?: -1L

    private val _state = MutableStateFlow(ReviewEditState())
    val state: StateFlow<ReviewEditState> = _state.asStateFlow()

    private val _sources = MutableStateFlow<List<Source>>(emptyList())
    val sources: StateFlow<List<Source>> = _sources.asStateFlow()

    private val _categories = MutableStateFlow<List<DescriptorCategory>>(emptyList())
    val categories: StateFlow<List<DescriptorCategory>> = _categories.asStateFlow()


    val descriptorsWithCategories: StateFlow<List<DescriptorWithCategory>> = combine(
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
        viewModelScope.launch {
            _sources.value = sourceRepo.getAllSources()
            _categories.value = categoryRepo.getAllCategories()
        }

        if (reviewId != -1L) {
            viewModelScope.launch {
                val review = reviewRepo.getReviewById(reviewId)
                val descriptorIds = reviewRepo.getDescriptorIdsByReviewId(reviewId)
                _state.value = ReviewEditState(
                    sourceId = review.reviewSourceID,
                    url = review.reviewUrl ?: "",
                    selectedDescriptorIds = descriptorIds.toSet()
                )
            }
        }
        viewModelScope.launch {
            descriptorsWithCategories.collect { list ->
                Log.d("ReviewEditVM", "Descriptors count: ${list.size}")
            }
        }
    }

    fun onSourceChanged(sourceId: Long) { _state.update { it.copy(sourceId = sourceId) } }
    fun onUrlChanged(url: String) { _state.update { it.copy(url = url) } }
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
            if (st.sourceId == -1L) return@launch

            val review = Review(
                reviewID = if (reviewId == -1L) 0L else reviewId,
                reviewedDrinkID = drinkId,
                reviewSourceID = st.sourceId,
                reviewUrl = st.url.ifBlank { null }
            )
            val resultId = if (reviewId == -1L) {
                reviewRepo.insertReview(review)
            } else {
                reviewRepo.updateReview(review)
                reviewId
            }
            Log.d("ReviewEditVM", "Saving review: resultId=$resultId, descriptorIds=${st.selectedDescriptorIds}")
            reviewRepo.updateReviewDescriptors(resultId, st.selectedDescriptorIds.toList())
            _saveSuccess.send(Unit)
        }
    }

    fun deleteReview() {
        viewModelScope.launch {
            reviewRepo.deleteReviewById(reviewId)
            _saveSuccess.send(Unit)
        }
    }
}