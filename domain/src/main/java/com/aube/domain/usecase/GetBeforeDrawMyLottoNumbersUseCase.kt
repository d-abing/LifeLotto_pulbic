package com.aube.domain.usecase

import com.aube.domain.model.MyLottoSet
import com.aube.domain.repository.MyLottoRepository
import javax.inject.Inject

class GetBeforeDrawMyLottoNumbersUseCase @Inject constructor(
    private val repository: MyLottoRepository
) {
    suspend operator fun invoke(round: Int): List<MyLottoSet> {
        return repository.getBeforeDraw(round)
    }
}
