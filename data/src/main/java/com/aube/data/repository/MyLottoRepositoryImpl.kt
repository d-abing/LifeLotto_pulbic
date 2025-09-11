package com.aube.data.repository

import com.aube.data.database.dao.MyLottoNumbersDao
import com.aube.data.model.entity.MyLottoNumbersEntity
import com.aube.data.model.entity.toDomain
import com.aube.data.retrofit.LottoApiService
import com.aube.domain.model.LottoResult
import com.aube.domain.model.MyLottoSet
import com.aube.domain.repository.MyLottoNumbersRepository
import com.aube.domain.util.estimateLatestRound
import com.aube.domain.util.rankOf
import java.time.LocalDateTime
import javax.inject.Inject

class MyLottoNumbersRepositoryImpl @Inject constructor(
    private val api: LottoApiService,
    private val dao: MyLottoNumbersDao,
) : MyLottoNumbersRepository {

    override suspend fun saveMyNumbers(numbers: List<Int>) {
        val entity = MyLottoNumbersEntity(
            numbers = numbers,
            round = estimateLatestRound(),
            date = LocalDateTime.now()
        )
        dao.insert(entity)
    }

    override suspend fun getMyNumbersHistory(): List<MyLottoSet> {
        refreshAllRanks()
        return dao.getAll().map { it.toDomain() }
    }

    override suspend fun getBeforeDraw(latestDate: LocalDateTime): List<MyLottoSet> {
        return dao.getBeforeDraw(latestDate.toString()).map { it.toDomain() }
    }

    override suspend fun deleteMyNumbers(id: Int) {
        dao.deleteById(id)
    }

    private suspend fun refreshAllRanks() {
        val entities = dao.getAll()
        if (entities.isEmpty()) return

        val cache = mutableMapOf<Int, LottoResult>()

        for (e in entities) {
            val result = cache.getOrPut(e.round) {
                val r = api.getLottoResult(e.round)
                LottoResult(
                    round = e.round,
                    date = r.date,
                    winningNumbers = listOf(r.no1, r.no2, r.no3, r.no4, r.no5, r.no6),
                    bonus = r.bonus,
                    firstPrize = r.firstPrize,
                    firstCount = r.firstCount
                )
            }
            val newRank = rankOf(e.numbers, result.winningNumbers, result.bonus)
            if (newRank != e.rank) {
                dao.updateRank(e.id, newRank)
            }
        }
    }
}
