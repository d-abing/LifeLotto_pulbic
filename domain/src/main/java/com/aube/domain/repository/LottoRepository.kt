package com.aube.domain.repository

import com.aube.domain.model.LottoSet
import kotlinx.coroutines.flow.Flow

interface LottoRepository {
    fun getSavedNumbers(): Flow<List<LottoSet>>
    suspend fun save(numbers: List<Int>, note: String? = null)
}
