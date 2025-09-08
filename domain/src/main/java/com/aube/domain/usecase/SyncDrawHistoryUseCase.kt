package com.aube.domain.usecase

import com.aube.domain.repository.LottoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class SyncDrawHistoryUseCase(
    private val repo: LottoRepository,
    private val io: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(
        targetRound: Int? = null,      // null이면 원격 최신 회차까지
        throttleMs: Long = 50L,        // 호출 간격
        fromRoundOverride: Int? = null // 강제로 특정 지점부터 받아오고 싶을 때
    ): Int { // 실제로 동기화한 건수 반환
        return withContext(io) {
            val remoteLatest = targetRound ?: repo.getLatestRemoteRound()
            val localLatest = fromRoundOverride ?: (repo.getLatestLocalRound() ?: 0)
            if (remoteLatest <= localLatest) return@withContext 0

            var synced = 0
            for (r in (localLatest + 1)..remoteLatest) {
                repo.fetchDraw(r)?.let { draw ->
                    repo.upsert(draw)
                    synced++
                }
                if (throttleMs > 0) delay(throttleMs)
            }
            synced
        }
    }
}
