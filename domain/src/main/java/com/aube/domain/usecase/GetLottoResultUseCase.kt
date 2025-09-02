package com.aube.domain.usecase

import com.aube.domain.model.LottoResult
import com.aube.domain.repository.LottoRepository

class GetLottoResultUseCase(
    private val repository: LottoRepository
) {
    suspend operator fun invoke(round: Int): LottoResult {
        return repository.getLottoResult(round)
    }
}
