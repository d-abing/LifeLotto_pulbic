package com.aube.domain.repository

import com.aube.domain.model.LottoDraw
import com.aube.domain.model.LottoResult
import kotlinx.coroutines.flow.Flow

interface LottoRepository {
    suspend fun getLottoResult(round: Int): LottoResult?
    fun observeAll(): Flow<List<LottoDraw>>
    suspend fun getLatestLocalRound(): Int?
    suspend fun upsert(draw: LottoDraw)
    suspend fun upsertAll(draws: List<LottoDraw>)
    suspend fun fetchDraw(round: Int): LottoDraw?
    suspend fun getLatestRemoteRound(): Int
}
