package com.aube.domain.usecase

import com.aube.domain.repository.RecommendRepository

class DeleteRecommendedNumbersUseCase(
    private val repository: RecommendRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteById(id)
    }
}
