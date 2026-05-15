package com.example.laykasommelier.viewModels
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.entities.Descriptor
import com.example.laykasommelier.data.local.entities.DescriptorCategory
import com.example.laykasommelier.data.local.entities.Ingredient
import com.example.laykasommelier.data.local.pojo.DescriptorWithCategory
import com.example.laykasommelier.data.local.pojo.editstates.IngredientEditState
import com.example.laykasommelier.data.local.repositories.DescriptorCategoryRepository
import com.example.laykasommelier.data.local.repositories.DescriptorRepository
import com.example.laykasommelier.data.local.repositories.IngredientRepository
import com.example.laykasommelier.network.ApiService
import com.example.laykasommelier.network.ImageUploadResponse
import com.example.laykasommelier.network.dto.IngredientCreateRequest
import com.example.laykasommelier.network.dto.IngredientDescriptorLinkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class IngredientEditViewModel @Inject constructor(
    private val ingredientRepo: IngredientRepository,
    private val descriptorRepo: DescriptorRepository,
    private val categoryRepo: DescriptorCategoryRepository,
    private val apiService: ApiService,
    @ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var ingredientId: Long = savedStateHandle["ingredientId"] ?: -1L

    private val _state = MutableStateFlow(IngredientEditState())
    val state: StateFlow<IngredientEditState> = _state.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _saveSuccess = Channel<Unit>(Channel.BUFFERED)
    val saveSuccess: Flow<Unit> = _saveSuccess.receiveAsFlow()

    private val _categories = MutableStateFlow<List<DescriptorCategory>>(emptyList())
    val categories: StateFlow<List<DescriptorCategory>> = _categories.asStateFlow()

    private val allDescriptors: Flow<List<DescriptorWithCategory>> =
        descriptorRepo.getAllDescriptorsWithCategories()
    val filteredDescriptors: StateFlow<List<DescriptorWithCategory>> =
        combine(
            allDescriptors,
            state.map { it.selectedCategoryId to it.searchQuery }
        ) { descriptors, (selectedCategoryId, searchQuery) ->
            descriptors.filter { desc ->
                val matchesCategory = selectedCategoryId == null || desc.categoryId == selectedCategoryId
                val matchesSearch = searchQuery.isBlank() ||
                        desc.descriptorName.contains(searchQuery, ignoreCase = true)
                matchesCategory && matchesSearch
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Запоминаем исходный набор дескрипторов для последующего сравнения
    private var initialDescriptorIds: Set<Long> = emptySet()
    init {
        viewModelScope.launch {
            _categories.value = categoryRepo.getAllCategories()
        }

        if (ingredientId != -1L) {
            viewModelScope.launch {
                val ing = ingredientRepo.getById(ingredientId)
                val descriptorIds = ingredientRepo.getDescriptorIdsForIngredient(ingredientId)
                initialDescriptorIds = descriptorIds
                _state.value = IngredientEditState(
                    name = ing.ingredientName,
                    acidity = ing.ingredientAcidity.toString(),
                    sugarLevel = ing.ingredientSugarLevel.toString(),
                    abv = ing.ingredientAbv.toString(),
                    imageUrl = ing.ingredientImageUrl,
                    selectedDescriptorIds = descriptorIds
                )
            }
        }
    }

    // --- Методы изменения состояния ---
    fun onNameChanged(name: String) { _state.update { it.copy(name = name) } }
    fun onAcidityChanged(v: String) { _state.update { it.copy(acidity = v) } }
    fun onSugarChanged(v: String) { _state.update { it.copy(sugarLevel = v) } }
    fun onAbvChanged(v: String) { _state.update { it.copy(abv = v) } }
    fun onImageSelected(uri: Uri) { _selectedImageUri.value = uri }
    fun clearSelectedImage() { _selectedImageUri.value = null }

    fun onSearchChanged(query: String) { _state.update { it.copy(searchQuery = query) } }

    fun onCategoryFilterSelected(categoryId: Long?) {
        _state.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun onDescriptorToggled(descriptorId: Long) {
        _state.update { state ->
            val selected = state.selectedDescriptorIds.toMutableSet()
            if (selected.contains(descriptorId)) selected.remove(descriptorId)
            else selected.add(descriptorId)
            state.copy(selectedDescriptorIds = selected)
        }
    }

    // --- Сохранение ---
    fun save() {
        viewModelScope.launch {
            val st = _state.value
            if (st.name.isBlank()) return@launch

            var finalImageUrl = st.imageUrl

            val localUri = _selectedImageUri.value
            if (localUri != null) {
                try {
                    val response = uploadImage(localUri)
                    finalImageUrl = response.url
                    _state.update { it.copy(imageUrl = finalImageUrl) }
                    _selectedImageUri.value = null
                } catch (e: Exception) {
                    Log.e("IngredientEdit", "Image upload failed", e)
                    return@launch
                }
            }

            val request = IngredientCreateRequest(
                name = st.name,
                acidity = st.acidity.toDoubleOrNull() ?: 7.0,
                sugarLevel = st.sugarLevel.toDoubleOrNull() ?: 0.0,
                abv = st.abv.toDoubleOrNull() ?: 0.0,
                imageUrl = finalImageUrl
            )

            try {
                if (ingredientId == -1L) {
                    // Создание нового ингредиента
                    Log.d("IngredientEdit", "Creating new ingredient")
                    val created = apiService.createIngredient(request)
                    ingredientId = created.id
                    ingredientRepo.insertIngredient(
                        Ingredient(
                            ingredientID = created.id,
                            ingredientName = created.name,
                            ingredientAcidity = created.acidity,
                            ingredientSugarLevel = created.sugarLevel,
                            ingredientAbv = created.abv,
                            ingredientImageUrl = created.imageUrl
                        )
                    )
                    // Отправляем каждый дескриптор на сервер с перехватом ошибок
                    st.selectedDescriptorIds.forEach { descId ->
                        try {
                            Log.d("IngredientEdit", "Calling API: addIngredientDescriptor($ingredientId, $descId)")
                            apiService.addIngredientDescriptor(
                                IngredientDescriptorLinkRequest(ingredientId, descId)
                            )
                            Log.d("IngredientEdit", "Successfully added descriptor $descId")
                        } catch (e: Exception) {
                            Log.e("IngredientEdit", "Failed to add descriptor $descId", e)
                        }
                    }
                    // Локально сохраняем дескрипторы в любом случае
                    ingredientRepo.updateIngredientDescriptors(ingredientId, st.selectedDescriptorIds.toList())
                    Log.d("IngredientEdit", "Locally saved ${st.selectedDescriptorIds.size} descriptors")
                } else {
                    // Обновление существующего ингредиента
                    Log.d("IngredientEdit", "Updating ingredient $ingredientId")
                    apiService.updateIngredient(ingredientId, request)
                    ingredientRepo.updateIngredient(
                        Ingredient(
                            ingredientID = ingredientId,
                            ingredientName = st.name,
                            ingredientAcidity = st.acidity.toDoubleOrNull() ?: 7.0,
                            ingredientSugarLevel = st.sugarLevel.toDoubleOrNull() ?: 0.0,
                            ingredientAbv = st.abv.toDoubleOrNull() ?: 0.0,
                            ingredientImageUrl = finalImageUrl
                        )
                    )

                    // Синхронизация дескрипторов с сервером
                    val selected = st.selectedDescriptorIds
                    val added = selected - initialDescriptorIds
                    val removed = initialDescriptorIds - selected
                    Log.d("IngredientEdit", "Added: $added, Removed: $removed")

                    added.forEach { descId ->
                        try {
                            Log.d("IngredientEdit", "Calling API: addIngredientDescriptor($ingredientId, $descId)")
                            apiService.addIngredientDescriptor(
                                IngredientDescriptorLinkRequest(ingredientId, descId)
                            )
                            Log.d("IngredientEdit", "Successfully added descriptor $descId")
                        } catch (e: Exception) {
                            Log.e("IngredientEdit", "Failed to add descriptor $descId", e)
                        }
                    }
                    removed.forEach { descId ->
                        try {
                            Log.d("IngredientEdit", "Calling API: removeIngredientDescriptor($ingredientId, $descId)")
                            apiService.removeIngredientDescriptor(ingredientId, descId)
                            Log.d("IngredientEdit", "Successfully removed descriptor $descId")
                        } catch (e: Exception) {
                            Log.e("IngredientEdit", "Failed to remove descriptor $descId", e)
                        }
                    }

                    // Локально обновляем дескрипторы
                    ingredientRepo.updateIngredientDescriptors(ingredientId, selected.toList())
                    initialDescriptorIds = selected
                }

                _saveSuccess.send(Unit)
            } catch (e: Exception) {
                Log.e("IngredientEdit", "Server save failed", e)
            }
        }
    }

    // --- Загрузка изображения ---
    private suspend fun uploadImage(uri: Uri): ImageUploadResponse {
        val file = uriToFile(uri)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
        return apiService.uploadImage(part)
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = appContext.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open image")
        val tempFile = File.createTempFile("ingredient_image", ".jpg", appContext.cacheDir)
        tempFile.deleteOnExit()
        FileOutputStream(tempFile).use { output ->
            inputStream.copyTo(output)
        }
        return tempFile
    }
}