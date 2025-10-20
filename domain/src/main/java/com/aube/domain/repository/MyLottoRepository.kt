package com.aube.domain.repository

import com.aube.domain.model.MyLottoSet

interface MyLottoRepository {
    suspend fun saveMyNumbers(numbers: List<Int>, round: Int?, rank: Int?)
    suspend fun getMyNumbersHistory(): List<MyLottoSet>
    suspend fun getMyNumbers(round: Int): List<MyLottoSet>
    suspend fun getBeforeDraw(round: Int): List<MyLottoSet>
    suspend fun deleteMyNumber(id: Int)
    suspend fun deleteBeforeDraw(round: Int)
    suspend fun deleteAll()
}
