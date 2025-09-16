package com.aube.data.repository

import com.aube.data.database.dao.LottoDrawDao
import com.aube.data.model.entity.toDomain
import com.aube.data.model.entity.toEntity
import com.aube.data.model.response.toDomain
import com.aube.data.retrofit.LottoApiService
import com.aube.domain.model.LottoDraw
import com.aube.domain.model.LottoResult
import com.aube.domain.repository.LottoRepository
import com.aube.domain.util.estimateLatestDateTime
import com.aube.domain.util.estimateLatestRound
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class LottoRepositoryImpl @Inject constructor(
    private val api: LottoApiService,
    private val dao: LottoDrawDao
) : LottoRepository {
    override suspend fun getLottoResult(round: Int): LottoResult = api.getLottoResult(round).toDomain()

    override fun observeAll(): Flow<List<LottoDraw>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getLatestLocalRound(): Int? = dao.getLatestRound()

    override suspend fun upsert(draw: LottoDraw) =
        dao.upsert(draw.toEntity())

    override suspend fun upsertAll(draws: List<LottoDraw>) =
        draws.forEach { upsert(it) }

    override suspend fun fetchDraw(round: Int): LottoDraw? {
        val dto = runCatching { api.getLottoResult(round) }.getOrNull() ?: return null

        val nums = listOfNotNull(dto.no1, dto.no2, dto.no3, dto.no4, dto.no5, dto.no6)
        if (dto.round != round || nums.size != 6) return null

        val parsedDateTime = runCatching {
            LocalDate.parse(dto.date, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay()
        }.getOrElse {
            estimateLatestDateTime()
        }

        return LottoDraw(
            round = round,
            numbers = nums,
            date = parsedDateTime
        )
    }

    override suspend fun getLatestRemoteRound(): Int {
        return estimateLatestRound()
    }
}
