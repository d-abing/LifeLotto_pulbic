package com.aube.domain.repository

import com.aube.domain.model.LottoSet
import kotlinx.coroutines.flow.Flow

interface RecommendRepository {
    fun getSavedNumbers(): Flow<List<LottoSet>>
    suspend fun save(numbers: List<Int>)
    suspend fun deleteById(id: Int)
    suspend fun deleteAll()
}
