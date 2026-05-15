package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.dao.SuggestionDao
import com.example.laykasommelier.data.local.entities.Suggestion
import com.example.laykasommelier.data.local.pojo.CocktailDescriptors
import com.example.laykasommelier.data.local.pojo.SuggestionPreviewItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SuggestionRepository@Inject constructor(
    private val suggestionDao: SuggestionDao
) {
    fun getSuggestionPreviews(): Flow<List<SuggestionPreviewItem>> {
        return suggestionDao.getSuggestionRows().map { rows ->
            rows.groupBy { it.suggestionId }.map { (suggestionId, group) ->
                val first = group.first()
                SuggestionPreviewItem(
                    suggestionId = suggestionId,
                    cocktailId = first.cocktailId,
                    cocktailName = first.cocktailName,
                    employeeName = first.employeeName,
                    status = first.status,
                    imageUrl = first.cocktailImageUrl,
                    descriptors = group
                        .filter { it.descriptorName != null && it.descriptorColor != null }
                        .map { CocktailDescriptors(it.descriptorName!!, it.descriptorColor!!) }
                        .distinct()
                )
            }
        }
    }
    fun getSuggestionById(id: Long): Flow<Suggestion?> = suggestionDao.getSuggestionById(id)
    suspend fun insertAll(list: List<Suggestion>) = suggestionDao.insertAll(list)
    suspend fun updateSuggestionStatus(id: Long, status: String) {
        suggestionDao.updateSuggestionStatus(id, status)
    }
}