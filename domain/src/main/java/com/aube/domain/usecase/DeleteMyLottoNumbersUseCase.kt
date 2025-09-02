package com.aube.domain.usecase

import com.aube.domain.repository.MyLottoNumbersRepository

class DeleteMyLottoNumbersUseCase(
    private val repository: MyLottoNumbersRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteMyNumbers(id)
    }
}
