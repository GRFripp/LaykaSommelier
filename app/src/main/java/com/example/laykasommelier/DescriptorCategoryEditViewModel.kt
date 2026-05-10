package com.example.laykasommelier

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.laykasommelier.data.local.repositories.DescriptorCategoryRepository
import com.example.laykasommelier.data.local.repositories.DescriptorRepository
import com.example.laykasommelier.data.local.repositories.IngredientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class IngredientEditState(
    val name: String = "",
    val acidity: String = "",
    val sugarLevel: String = "",
    val selectedDescriptorIds: Set<Long> = emptySet(),
    val searchQuery: String = "",
    val selectedCategoryId: Long? = null   // null = все категории
)
@HiltViewModel
class DescriptorCategoryEditViewModel @Inject constructor(
    private val ingredientRepo: IngredientRepository,
    private val descriptorRepo: DescriptorRepository,
    private val categoryRepo: DescriptorCategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
}