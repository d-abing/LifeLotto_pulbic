package com.aube.domain.usecase

import com.aube.domain.model.MyLottoSet
import com.aube.domain.repository.MyLottoNumbersRepository

class GetMyLottoNumbersHistoryUseCase(
    private val repository: MyLottoNumbersRepository
) {
    suspend operator fun invoke(): List<MyLottoSet> {
        return repository.getMyNumbersHistory()
    }
}
