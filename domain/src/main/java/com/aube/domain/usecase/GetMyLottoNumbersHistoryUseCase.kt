package com.aube.domain.usecase

import com.aube.domain.model.MyLottoSet
import com.aube.domain.repository.MyLottoRepository
import javax.inject.Inject

class GetMyLottoNumbersHistoryUseCase @Inject constructor(
    private val repository: MyLottoRepository
) {
    suspend operator fun invoke(): List<MyLottoSet> {
        return repository.getMyNumbersHistory()
    }
}
