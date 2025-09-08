package com.aube.domain.usecase

import com.aube.domain.model.LottoSet
import com.aube.domain.repository.MyLottoNumbersRepository
import java.time.LocalDateTime

class GetMyLottoNumbersUseCase(
    private val repository: MyLottoNumbersRepository
) {
    suspend operator fun invoke(latestDate: LocalDateTime): List<LottoSet> {
        return repository.getMyNumbers(latestDate)
    }
}
