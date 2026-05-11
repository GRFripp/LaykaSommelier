package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.dao.SourceDao
import com.example.laykasommelier.data.local.entities.Source

class SourceRepository(
    private val sourceDao: SourceDao
) {
    suspend fun getAllSources(): List<Source> = sourceDao.getAllSources()

    suspend fun getSourceById(id: Long): Source =
        sourceDao.getSourceById(id) ?: throw Exception("Источник не найден")

    suspend fun insertSource(source: Source): Long = sourceDao.insertSource(source)

    suspend fun updateSource(source: Source) = sourceDao.updateSource(source)

    suspend fun deleteSource(source: Source) = sourceDao.deleteSource(source)
}