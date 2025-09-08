package com.aube.domain.usecase

import com.aube.domain.model.LottoSet
import com.aube.domain.repository.RecommendRepository
import kotlinx.coroutines.flow.Flow

class GetRecommendedNumbersUseCase(
    private val repository: RecommendRepository
) {
    operator fun invoke(): Flow<List<LottoSet>> {
        return repository.getSavedNumbers()
    }
}
