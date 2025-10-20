package com.aube.domain.usecase

import com.aube.domain.repository.RecommendRepository
import javax.inject.Inject

class DeleteRecommendedNumberUseCase @Inject constructor(
    private val repository: RecommendRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteById(id)
    }
}
