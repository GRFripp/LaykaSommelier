package com.example.laykasommelier.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laykasommelier.data.local.entities.Drink
import com.example.laykasommelier.data.local.pojo.DrinkReviewItem
import com.example.laykasommelier.data.local.pojo.editstates.DrinkEditState
import com.example.laykasommelier.data.local.repositories.DrinkRepository
import com.example.laykasommelier.network.ApiService
import com.example.laykasommelier.network.ImageUploadResponse
import com.example.laykasommelier.network.dto.DrinkCreateRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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
class DrinkEditViewModel @Inject constructor(
    private val drinkRepo: DrinkRepository,
    private val apiService: ApiService,
    @ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var drinkId: Long = savedStateHandle["drinkId"] ?: -1L

    private val _state = MutableStateFlow(DrinkEditState())
    val state: StateFlow<DrinkEditState> = _state.asStateFlow()
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()
    // Поток рецензий: переключается, когда drinkId становится известен (после сохранения нового напитка)
    private val _drinkIdFlow = MutableStateFlow(drinkId)

    val reviews: StateFlow<List<DrinkReviewItem>> = _drinkIdFlow.flatMapLatest { id ->
        if (id != -1L) drinkRepo.getDrinkReviews(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _saveSuccess = Channel<Unit>(Channel.BUFFERED)
    val saveSuccess: Flow<Unit> = _saveSuccess.receiveAsFlow()

    init {
        if (drinkId != -1L) {
            loadDrink(drinkId)
        }
    }

    private fun loadDrink(id: Long) {
        viewModelScope.launch {
            val drink = drinkRepo.getDrinkById(id).first() // suspend функция, возвращает Drink
            Log.d("CHECK DRINK EDIT","drink: ${drink.name}")
            _state.value = DrinkEditState(
                name = drink.name,
                type = drink.type,
                subType = drink.subType ?: "",
                country = drink.country ?: "",
                producer = drink.producer ?: "",
                aged = drink.aged?.toString() ?: "",
                abv = drink.abv.toString(),
                imageUrl = drink.imageUrl ?: ""
            )
        }
    }

    fun onNameChanged(name: String) { _state.update { it.copy(name = name) } }
    fun onTypeChanged(type: String) { _state.update { it.copy(type = type) } }
    fun onSubTypeChanged(subType: String) { _state.update { it.copy(subType = subType) } }
    fun onCountryChanged(country: String) { _state.update { it.copy(country = country) } }
    fun onProducerChanged(producer: String) { _state.update { it.copy(producer = producer) } }
    fun onAgedChanged(aged: String) { _state.update { it.copy(aged = aged) } }
    fun onAbvChanged(abv: String) { _state.update { it.copy(abv = abv) } }
    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
    }

    fun clearSelectedImage() {
        _selectedImageUri.value = null
    }
    fun saveDrink() {
        viewModelScope.launch {
            val s = _state.value
            if (s.name.isBlank()) return@launch

            var finalImageUrl = s.imageUrl

            // Если выбрано новое локальное изображение — грузим его на сервер
            val localUri = _selectedImageUri.value
            if (localUri != null) {
                try {
                    val response = uploadImage(localUri)
                    finalImageUrl = response.url
                    _state.update { it.copy(imageUrl = finalImageUrl) }
                    _selectedImageUri.value = null
                    Log.d("DrinkEdit", "Image uploaded: $finalImageUrl")
                } catch (e: Exception) {
                    Log.e("DrinkEdit", "Image upload failed", e)
                    return@launch
                }
            }

            val request = DrinkCreateRequest(
                name = s.name,
                type = s.type,
                subType = s.subType.ifBlank { null },
                country = s.country.ifBlank { null },
                producer = s.producer.ifBlank { null },
                aged = s.aged.toIntOrNull() ?: 0,
                abv = s.abv.toDoubleOrNull() ?: 0.0,
                imageUrl = finalImageUrl ?: ""
            )

            try {
                if (drinkId == -1L) {
                    // Создание нового напитка на сервере
                    val createdDrink = apiService.createDrink(request)
                    drinkId = createdDrink.id
                    _drinkIdFlow.value = createdDrink.id
                    // Также сохраняем локально (опционально)
                    drinkRepo.insertDrink(
                        Drink(
                            drinkID = createdDrink.id,
                            drinkName = createdDrink.name,
                            drinkType = createdDrink.type,
                            drinkSubType = createdDrink.subType,
                            drinkCountry = createdDrink.country,
                            drinkProducer = createdDrink.producer,
                            drinkAged = createdDrink.aged,
                            drinkAbv = createdDrink.abv,
                            drinkImageUrl = createdDrink.imageUrl
                        )
                    )
                } else {

                    // Обновление существующего
                    apiService.updateDrink(drinkId, request)
                    // Обновляем локально
                    drinkRepo.updateDrink(
                        Drink(
                            drinkID = drinkId,
                            drinkName = s.name,
                            drinkType = s.type,
                            drinkSubType = s.subType.ifBlank { null },
                            drinkCountry = s.country.ifBlank { null },
                            drinkProducer = s.producer.ifBlank { null },
                            drinkAged = s.aged.toIntOrNull() ?: 0,
                            drinkAbv = s.abv.toDoubleOrNull() ?: 0.0,
                            drinkImageUrl = finalImageUrl ?: ""
                        )
                    )
                }
                _saveSuccess.send(Unit)
            } catch (e: Exception) {
                Log.e("DrinkEdit", "Server save failed", e)
            }
        }
    }

    fun refreshReviews() {
        _drinkIdFlow.value = drinkId // триггерит перезагрузку
    }
    private suspend fun uploadImage(uri: Uri): ImageUploadResponse {
        val file = uriToFile(uri)
        val requestBody = file.asRequestBody("file/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

        return apiService.uploadImage(part)
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = appContext.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open image")
        val tempFile = File.createTempFile("drink_image", ".jpg", appContext.cacheDir)
        tempFile.deleteOnExit()
        FileOutputStream(tempFile).use { output ->
            inputStream.copyTo(output)
        }
        return tempFile
    }
}