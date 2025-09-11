package com.aube.domain.usecase

import com.aube.domain.model.MyLottoSet
import com.aube.domain.repository.MyLottoNumbersRepository
import java.time.LocalDateTime

class GetMyLottoNumbersUseCase(
    private val repository: MyLottoNumbersRepository
) {
    suspend operator fun invoke(latestDate: LocalDateTime): List<MyLottoSet> {
        return repository.getBeforeDraw(latestDate)
    }
}
