package com.example.laykasommelier.data.local.repositories

import com.example.laykasommelier.data.local.dao.CocktailDao
import com.example.laykasommelier.data.local.pojo.CocktailDescriptors
import com.example.laykasommelier.data.local.pojo.CocktailListRow
import kotlinx.coroutines.flow.Flow
import com.example.laykasommelier.data.local.pojo.CocktailListPreviews
import com.example.laykasommelier.data.local.pojo.DrinkListPreviews
import kotlinx.coroutines.flow.map

class CocktailRepository(val cocktailDao: CocktailDao) {
    fun getCocktailPreviews(): Flow<List<CocktailListPreviews>> {
        return cocktailDao.getCocktailListRows().map{ rows ->
            rows.groupBy {it.cId}
                .map {
                        (cId, cRows)->
                    val first = cRows.first()
                    CocktailListPreviews(
                        cId,
                        first.cName,
                        imageUrl = null,
                        descriptors = cRows
                            .filter { it.dName != null && it.dColor != null }
                            .map{ CocktailDescriptors(it.dName!!,it.dColor!!) }
                    )
                }
        }

    }
}




