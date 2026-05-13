package com.example.laykasommelier

import android.app.Application
import android.util.Log
import com.example.laykasommelier.data.local.repositories.SyncRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class LaykaApplication : Application(){

    @Inject
    lateinit var syncRepository: SyncRepository

    override fun onCreate() {
        super.onCreate()

        // Запускаем синхронизацию данных с сервером в фоновом потоке
        CoroutineScope(Dispatchers.IO).launch {
            try {
                syncRepository.syncAll()
                Log.d("SYNC", "Данные успешно загружены с сервера")
            } catch (e: Exception) {
                Log.e("SYNC", "Ошибка синхронизации", e)
            }
        }
    }
}