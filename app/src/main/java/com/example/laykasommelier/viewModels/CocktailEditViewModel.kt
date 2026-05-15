package com.example.laykasommelier.viewModels

import android.content.Context
import android.net.Uri
import android.service.notification.Condition.newId
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.SessionManager
import com.example.laykasommelier.data.local.dao.CocktailIngredientDao
import com.example.laykasommelier.data.local.dao.MakingMethodDao
import com.example.laykasommelier.data.local.entities.Cocktail
import com.example.laykasommelier.data.local.entities.CocktailIngredient
import com.example.laykasommelier.data.local.entities.Ingredient
import com.example.laykasommelier.data.local.entities.MakingMethod
import com.example.laykasommelier.data.local.mapper.toEntity
import com.example.laykasommelier.data.local.pojo.CocktailIngredientLinkItem
import com.example.laykasommelier.data.local.pojo.EmployeeRole
import com.example.laykasommelier.data.local.pojo.editstates.CocktailEditState
import com.example.laykasommelier.data.local.repositories.CocktailRepository
import com.example.laykasommelier.network.ApiService
import com.example.laykasommelier.network.ImageUploadResponse
import com.example.laykasommelier.network.dto.CocktailCreateRequest
import com.example.laykasommelier.network.dto.CocktailIngredientLinkRequest
import com.example.laykasommelier.network.dto.SuggestionCreateRequest
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class CocktailEditViewModel @Inject constructor(
    private val cocktailRepo: CocktailRepository,
    private val makingMethodRepo: MakingMethodDao,
    private val apiService: ApiService,
    @ApplicationContext private val appContext: Context,
    private val sessionManager: SessionManager,
    private val cocktailIngredientDao: CocktailIngredientDao,   // новая зависимость
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val cocktailId: Long = savedStateHandle["cocktailId"] ?: -1L
    private var initialIngredientIds: Set<Long> = emptySet()
    private val _state = MutableStateFlow(CocktailEditState())
    val state: StateFlow<CocktailEditState> = _state.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    val makingMethods: StateFlow<List<MakingMethod>> = makingMethodRepo.getAllMakingMethods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Локальный список ингредиентов (работает независимо от cocktailId)
    private val _localIngredients = MutableStateFlow<List<CocktailIngredientLinkItem>>(emptyList())
    val ingredientLinks: StateFlow<List<CocktailIngredientLinkItem>> = _localIngredients.asStateFlow()

    private val _allIngredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val allIngredients: StateFlow<List<Ingredient>> = _allIngredients.asStateFlow()

    private val _saveSuccess = Channel<Unit>(Channel.BUFFERED)
    val saveSuccess: Flow<Unit> = _saveSuccess.receiveAsFlow()

    init {
        viewModelScope.launch {
            _allIngredients.value = cocktailRepo.getAllIngredients().first()
        }

        if (cocktailId != -1L) {
            viewModelScope.launch {
                val cocktail = cocktailRepo.getCocktail(cocktailId).first()
                _state.value = CocktailEditState(
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
                    imageUrl = cocktail.cocktailImageUrl ?: ""
                )
                // Загружаем существующие связи ингредиентов
                val existingLinks = cocktailRepo.getCocktailIngredientsLinks(cocktailId).first()
                _localIngredients.value = existingLinks
                initialIngredientIds = existingLinks.map { it.ingredientId }.toSet()
            }
        }
    }

    // --- Методы изменения полей ---
    fun onNameChanged(name: String) { _state.update { it.copy(name = name) } }
    fun onVolumeChanged(v: String) { _state.update { it.copy(volume = v) } }
    fun onAcidityChanged(v: String) { _state.update { it.copy(acidity = v) } }
    fun onSugarChanged(v: String) { _state.update { it.copy(sugarLevel = v) } }
    fun onAbvChanged(v: String) { _state.update { it.copy(abv = v) } }
    fun onGlassChanged(v: String) { _state.update { it.copy(glass = v) } }
    fun onMakingMethodChanged(id: Long) { _state.update { it.copy(makingMethodId = id) } }
    fun onDescriptionChanged(v: String) { _state.update { it.copy(description = v) } }
    fun onAuthorChanged(v: String) { _state.update { it.copy(author = v) } }
    fun onServingChanged(v: String) { _state.update { it.copy(serving = v) } }

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
    }

    fun clearSelectedImage() {
        _selectedImageUri.value = null
    }

    fun addIngredient(ingredientId: Long, volume: Double) {
        _localIngredients.update { currentList ->
            // Находим имя по ingredientId из загруженного списка всех ингредиентов
            val name = _allIngredients.value.find { it.ingredientID == ingredientId }?.ingredientName ?: "???"
            if (currentList.any { it.ingredientId == ingredientId }) {
                // Обновить объём у существующего
                currentList.map { if (it.ingredientId == ingredientId) it.copy(volume = volume, ingredientName = name) else it }
            } else {
                currentList + CocktailIngredientLinkItem(
                    ingredientId = ingredientId,
                    ingredientName = name,
                    volume = volume
                )
            }
        }
    }

    fun removeIngredient(ingredientId: Long) {
        _localIngredients.update { currentList -> currentList.filter { it.ingredientId != ingredientId } }
    }

    fun saveCocktail() {
        viewModelScope.launch {
            val s = _state.value
            if (s.name.isBlank()) return@launch

            var finalImageUrl = s.imageUrl

            val localUri = _selectedImageUri.value
            if (localUri != null) {
                try {
                    val response = uploadImage(localUri)
                    finalImageUrl = response.url
                    _state.update { it.copy(imageUrl = finalImageUrl) }
                    _selectedImageUri.value = null
                } catch (e: Exception) {
                    Log.e("CocktailEdit", "Image upload failed", e)
                    return@launch
                }
            }

            val request = CocktailCreateRequest(
                name = s.name,
                volume = s.volume.toDoubleOrNull() ?: 0.0,
                acidity = s.acidity.toDoubleOrNull() ?: 0.0,
                sugarLevel = s.sugarLevel.toDoubleOrNull() ?: 0.0,
                abv = s.abv.toDoubleOrNull() ?: 0.0,
                glass = s.glass,
                makingMethodId = s.makingMethodId,
                description = s.description,
                author = s.author,
                serving = s.serving,
                imageUrl = finalImageUrl
            )

            try {
                val role = sessionManager.getRole()
                if (role == EmployeeRole.MANAGER) {
                    if (cocktailId == -1L) {
                        // Создание нового коктейля
                        val created = apiService.createCocktail(request)
                        val newId = created.id
                        cocktailRepo.insertCocktail(created.toEntity())
                        saveIngredientsLocally(newId)
                        syncIngredientsWithServer(newId)   // все ингредиенты отправляем на сервер
                    } else {
                        // Обновление существующего
                        apiService.updateCocktail(cocktailId, request)
                        cocktailRepo.updateCocktail(request.toEntity(cocktailId))
                        saveIngredientsLocally(cocktailId)
                        syncIngredientsWithServer(cocktailId)   // синхронизируем изменения
                    }
                } else {
                    // Не менеджер – создаём коктейль и заявку
                    val created = apiService.createCocktail(request)
                    val newId = created.id
                    cocktailRepo.insertCocktail(created.toEntity())
                    saveIngredientsLocally(newId)
                    syncIngredientsWithServer(newId)

                    val suggestionRequest = SuggestionCreateRequest(
                        cocktailId = newId,
                        employeeId = sessionManager.getEmployeeId(),
                        status = "pending"
                    )
                    apiService.createSuggestion(suggestionRequest)
                }
                _saveSuccess.send(Unit)
            } catch (e: Exception) {
                Log.e("CocktailEdit", "Server save failed", e)
            }
        }
    }

    private suspend fun saveIngredientsLocally(cocktailId: Long) {
        cocktailIngredientDao.deleteAllLinksForCocktail(cocktailId)
        _localIngredients.value.forEach { link ->
            cocktailIngredientDao.insertLink(
                CocktailIngredient(
                    cocktailID = cocktailId,
                    ingredientID = link.ingredientId,
                    ingredientVolume = link.volume
                )
            )
        }
    }

    private suspend fun syncIngredientsWithServer(cocktailId: Long) {
        val selectedIds = _localIngredients.value.map { it.ingredientId }.toSet()
        val added = selectedIds - initialIngredientIds
        val removed = initialIngredientIds - selectedIds

        added.forEach { ingId ->
            val volume = _localIngredients.value.find { it.ingredientId == ingId }?.volume ?: 0.0
            apiService.addIngredientToCocktail(
                CocktailIngredientLinkRequest(
                    cocktailId,
                    ingId,
                    volume
                )
            )
        }
        removed.forEach { ingId ->
            apiService.removeIngredientFromCocktail(cocktailId, ingId)
        }
        initialIngredientIds = selectedIds
    }

    private suspend fun uploadImage(uri: Uri): ImageUploadResponse {
        val file = uriToFile(uri)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
        return apiService.uploadImage(part)
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = appContext.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open image")
        val tempFile = File.createTempFile("cocktail_image", ".jpg", appContext.cacheDir)
        tempFile.deleteOnExit()
        FileOutputStream(tempFile).use { output ->
            inputStream.copyTo(output)
        }
        return tempFile
    }
}