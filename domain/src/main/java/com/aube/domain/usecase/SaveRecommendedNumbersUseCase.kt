package com.aube.domain.usecase

import com.aube.domain.repository.RecommendRepository

class SaveRecommendedNumbersUseCase(
    private val repository: RecommendRepository
) {
    suspend operator fun invoke(numbers: List<Int>) {
        repository.save(numbers)
    }
}
