package com.aube.domain.usecase

import com.aube.domain.model.LottoDraw
import com.aube.domain.repository.LottoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDrawHistoryUseCase @Inject constructor(
    private val repo: LottoRepository
) {
    operator fun invoke(): Flow<List<LottoDraw>> = repo.observeAll()
}
