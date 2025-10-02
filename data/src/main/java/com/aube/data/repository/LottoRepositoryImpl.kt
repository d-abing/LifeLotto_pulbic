package com.aube.data.repository

import com.aube.data.database.dao.LottoDrawDao
import com.aube.data.model.entity.toDomain
import com.aube.data.model.entity.toEntity
import com.aube.data.model.response.toDomainOrNull
import com.aube.data.retrofit.LottoApiService
import com.aube.domain.model.LottoDraw
import com.aube.domain.model.LottoResult
import com.aube.domain.repository.LottoRepository
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
    override suspend fun getLottoResult(round: Int): LottoResult? =
        runCatching { api.getLottoResult(round).toDomainOrNull() }.getOrNull()

    override fun observeAll(): Flow<List<LottoDraw>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getLatestLocalRound(): Int? = dao.getLatestRound()

    override suspend fun upsert(draw: LottoDraw) =
        dao.upsert(draw.toEntity())

    override suspend fun upsertAll(draws: List<LottoDraw>) =
        draws.forEach { upsert(it) }

    override suspend fun fetchDraw(round: Int): LottoDraw? {
        // 1) 네트워크 예외 → null
        val dto = runCatching { api.getLottoResult(round) }.getOrNull() ?: return null

        // 2) (선택) returnValue가 오면 success만 통과
        if (dto.returnValue != null && dto.returnValue != "success") return null

        // 3) 회차/필수값 완전성 체크
        val dtoRound = dto.round ?: return null
        if (dtoRound != round) return null

        val numsNullable = listOf(dto.no1, dto.no2, dto.no3, dto.no4, dto.no5, dto.no6)
        if (numsNullable.any { it == null }) return null
        val nums = numsNullable.filterNotNull() // 6개 확정

        val dateStr = dto.date?.takeIf { it.isNotBlank() } ?: return null
        val parsedDateTime = runCatching {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay()
        }.getOrElse { return null } // 파싱 실패도 발표 전/불완전으로 간주

        // 4) 모두 OK일 때만 도메인 생성
        return LottoDraw(
            round = dtoRound,
            numbers = nums,
            date = parsedDateTime
        )
    }

    override suspend fun getLatestRemoteRound(): Int {
        return estimateLatestRound()
    }
}
