package com.aube.domain.repository

import com.aube.domain.model.LottoSet
import java.time.LocalDateTime

interface MyLottoNumbersRepository {
    suspend fun saveMyNumbers(numbers: List<Int>)
    suspend fun getMyNumbersHistory(): List<LottoSet>
    suspend fun getMyNumbers(latestDate: LocalDateTime): List<LottoSet>
    suspend fun deleteMyNumbers(id: Int)
}
