package com.aube.domain.usecase

import com.aube.domain.repository.MyLottoRepository
import javax.inject.Inject

class DeleteAllBeforeDrawMyLottoNumbersUseCase @Inject constructor(
    private val repository: MyLottoRepository
) {
    suspend operator fun invoke(round: Int) {
        repository.deleteBeforeDraw(round)
    }
}
