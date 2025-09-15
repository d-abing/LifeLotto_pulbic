package com.aube.domain.repository

import com.aube.domain.model.MyLottoSet

interface MyLottoNumbersRepository {
    suspend fun saveMyNumbers(numbers: List<Int>, round: Int?, rank: Int?)
    suspend fun getMyNumbersHistory(): List<MyLottoSet>
    suspend fun getBeforeDraw(round: Int): List<MyLottoSet>
    suspend fun deleteMyNumbers(id: Int)
}
