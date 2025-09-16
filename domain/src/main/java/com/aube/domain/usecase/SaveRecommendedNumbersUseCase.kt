package com.aube.domain.usecase

import com.aube.domain.repository.RecommendRepository
import javax.inject.Inject

class SaveRecommendedNumbersUseCase @Inject constructor(
    private val repository: RecommendRepository
) {
    suspend operator fun invoke(numbers: List<Int>) {
        repository.save(numbers)
    }
}
