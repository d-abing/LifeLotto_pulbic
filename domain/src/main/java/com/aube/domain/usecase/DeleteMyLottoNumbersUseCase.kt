package com.aube.domain.usecase

import com.aube.domain.repository.MyLottoRepository
import javax.inject.Inject

class DeleteMyLottoNumbersUseCase @Inject constructor(
    private val repository: MyLottoRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteMyNumbers(id)
    }
}
