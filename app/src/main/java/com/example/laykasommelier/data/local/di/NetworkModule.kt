package com.example.laykasommelier.data.local.di

import com.example.laykasommelier.network.ApiService
import com.example.laykasommelier.network.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideApiService(): ApiService = RetrofitClient.instance
}