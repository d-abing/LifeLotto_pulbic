package com.aube.domain.usecase

import com.aube.domain.model.LottoDraw
import com.aube.domain.repository.LottoRepository
import kotlinx.coroutines.flow.Flow

class ObserveDrawHistoryUseCase(
    private val repo: LottoRepository
) {
    operator fun invoke(): Flow<List<LottoDraw>> = repo.observeAll()
}
