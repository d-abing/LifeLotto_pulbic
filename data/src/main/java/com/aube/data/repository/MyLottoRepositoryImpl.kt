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

        for (e in entities) {
            if (e.rank != null || e.round > latest) continue
            val result = cache[e.round] ?: run {
                val dto = runCatching { api.getLottoResult(e.round) }.getOrNull()
                val nums = dto?.let { listOfNotNull(it.no1, it.no2, it.no3, it.no4, it.no5, it.no6) }
                if (dto == null || nums == null || nums.size != 6) null else {
                    val mapped = LottoResult(
                        round = e.round,
                        date = dto.date,
                        winningNumbers = nums,
                        bonus = dto.bonus,
                        firstPrize = dto.firstPrize,
                        firstCount = dto.firstCount
                    )
                    cache[e.round] = mapped
                    mapped
                }
            } ?: continue

            val newRank = rankOf(e.numbers, result.winningNumbers, result.bonus)
            if (newRank != null) {
                dao.updateRank(e.id, newRank)
            }
        }
    }
}
