package com.aube.domain.usecase

import com.aube.domain.model.MyLottoSet
import com.aube.domain.repository.MyLottoNumbersRepository

class GetMyLottoNumbersUseCase(
    private val repository: MyLottoNumbersRepository
) {
    suspend operator fun invoke(round: Int): List<MyLottoSet> {
        return repository.getBeforeDraw(round)
    }
}
