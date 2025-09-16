package com.aube.domain.usecase

import com.aube.domain.repository.MyLottoRepository
import javax.inject.Inject

class SaveMyLottoNumbersUseCase @Inject constructor(
    private val repository: MyLottoRepository
) {
    suspend operator fun invoke(numbers: List<Int>, round: Int? = null, rank: Int? = null) {
        repository.saveMyNumbers(numbers, round, rank)
    }
}
