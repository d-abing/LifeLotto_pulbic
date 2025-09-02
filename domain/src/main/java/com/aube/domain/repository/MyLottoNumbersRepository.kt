package com.aube.domain.repository

import com.aube.domain.model.MyLottoNumbers

interface MyLottoNumbersRepository {
    suspend fun saveMyNumbers(numbers: List<List<Int>>)
    suspend fun getMyNumbersHistory(): List<MyLottoNumbers>
    suspend fun getMyNumbers(): MyLottoNumbers?
    suspend fun deleteMyNumbers(id: Int)
}
