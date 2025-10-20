package com.aube.domain.usecase

import com.aube.domain.repository.MyLottoRepository
import javax.inject.Inject

class DeleteMyLottoNumberUseCase @Inject constructor(
    private val repository: MyLottoRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteMyNumber(id)
    }
}
