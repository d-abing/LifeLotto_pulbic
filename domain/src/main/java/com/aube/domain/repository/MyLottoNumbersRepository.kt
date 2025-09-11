package com.aube.domain.repository

import com.aube.domain.model.MyLottoSet
import java.time.LocalDateTime

interface MyLottoNumbersRepository {
    suspend fun saveMyNumbers(numbers: List<Int>)
    suspend fun getMyNumbersHistory(): List<MyLottoSet>
    suspend fun getBeforeDraw(latestDate: LocalDateTime): List<MyLottoSet>
    suspend fun deleteMyNumbers(id: Int)
}
