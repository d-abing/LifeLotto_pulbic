package com.aube.domain.usecase

import com.aube.domain.model.MyLottoNumbers
import com.aube.domain.repository.MyLottoNumbersRepository

class GetMyLottoNumbersUseCase(
    private val repository: MyLottoNumbersRepository
) {
    suspend operator fun invoke(): MyLottoNumbers? {
        return repository.getMyNumbers()
    }
}
