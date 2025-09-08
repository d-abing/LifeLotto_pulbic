package com.aube.domain.usecase

import com.aube.domain.model.LottoSet
import com.aube.domain.repository.MyLottoNumbersRepository

class GetMyLottoNumbersHistoryUseCase(
    private val repository: MyLottoNumbersRepository
) {
    suspend operator fun invoke(): List<LottoSet>? {
        return repository.getMyNumbersHistory()
    }
}
