package com.aube.data.repository

import com.aube.data.database.dao.MyLottoDao
import com.aube.data.model.entity.MyLottoNumbersEntity
import com.aube.data.model.entity.toDomain
import com.aube.data.retrofit.LottoApiService
import com.aube.domain.model.LottoResult
import com.aube.domain.model.MyLottoSet
import com.aube.domain.repository.MyLottoRepository
import com.aube.domain.util.estimateLatestRound
import com.aube.domain.util.rankOf
import java.time.LocalDateTime
import javax.inject.Inject

class MyLottoRepositoryImpl @Inject constructor(
    private val api: LottoApiService,
    private val dao: MyLottoDao,
) : MyLottoRepository {

    override suspend fun saveMyNumbers(numbers: List<Int>, round: Int?, rank: Int?) {
        val entity = MyLottoNumbersEntity(
            numbers = numbers,
            round = round ?: (estimateLatestRound() + 1),
            date = LocalDateTime.now(),
            rank = rank
        )
        dao.insert(entity)
    }

    override suspend fun getMyNumbersHistory(): List<MyLottoSet> {
        refreshAllRanks()
        return dao.getAll().map { it.toDomain() }
    }

    override suspend fun getBeforeDraw(round: Int): List<MyLottoSet> {
        return dao.getBeforeDraw(round).map { it.toDomain() }
    }

    override suspend fun deleteMyNumbers(id: Int) {
        dao.deleteById(id)
    }

    private suspend fun refreshAllRanks() {
        val entities = dao.getAll()
        if (entities.isEmpty()) return

        val latest = estimateLatestRound()
        val cache = mutableMapOf<Int, LottoResult>()

        suspend fun fetchSafe(round: Int): LottoResult? {
            cache[round]?.let { return it }

            val dto = runCatching { api.getLottoResult(round) }.getOrNull() ?: return null

            // (선택) returnValue가 있다면 success만 통과
            if (dto.returnValue != null && dto.returnValue != "success") return null

            // 회차 일치 확인 (서버가 값을 주면 검증, 없으면 스킵)
            val dtoRound = dto.round
            if (dtoRound != null && dtoRound != round) return null

            // 번호 6개/보너스/날짜 모두 유효할 때만 매핑
            val numsNullable = listOf(dto.no1, dto.no2, dto.no3, dto.no4, dto.no5, dto.no6)
            if (numsNullable.any { it == null }) return null
            val numbers = numsNullable.filterNotNull()

            val bonus = dto.bonus ?: return null
            val dateStr = dto.date?.takeIf { it.isNotBlank() } ?: return null

            val mapped = LottoResult(
                round = dtoRound ?: round,
                date = dateStr,                             // String 유지(또는 여기서 LocalDate 파싱)
                winningNumbers = numbers,
                bonus = bonus,
                firstPrize = dto.firstPrize ?: 0L,
                firstCount = dto.firstCount ?: 0
            )
            cache[round] = mapped
            return mapped
        }

        for (e in entities) {
            // 이미 등수 있음, 또는 미래 회차는 스킵
            if (e.rank != null || e.round > latest) continue

            val result = fetchSafe(e.round) ?: continue

            val newRank = rankOf(e.numbers, result.winningNumbers, result.bonus)
            if (newRank != null) {
                dao.updateRank(e.id, newRank)
            }
        }
    }
}
