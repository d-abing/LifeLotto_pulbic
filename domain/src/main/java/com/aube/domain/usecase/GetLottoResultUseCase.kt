package com.aube.domain.usecase

import com.aube.domain.model.LottoResult
import com.aube.domain.repository.LottoRepository
import javax.inject.Inject

class GetLottoResultUseCase @Inject constructor(
    private val repository: LottoRepository
) {
    suspend operator fun invoke(round: Int): LottoResult? {
        return repository.getLottoResult(round)
    }
}
