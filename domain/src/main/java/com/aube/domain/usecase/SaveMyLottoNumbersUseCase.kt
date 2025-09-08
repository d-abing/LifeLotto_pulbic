package com.aube.domain.usecase

import com.aube.domain.repository.MyLottoNumbersRepository

class SaveMyLottoNumbersUseCase(
    private val repository: MyLottoNumbersRepository
) {
    suspend operator fun invoke(numbers: List<Int>) {
        repository.saveMyNumbers(numbers)
    }
}
