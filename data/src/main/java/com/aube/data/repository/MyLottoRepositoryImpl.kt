package com.aube.data.repository

import com.aube.data.database.dao.MyLottoNumbersDao
import com.aube.data.model.entity.MyLottoNumbersEntity
import com.aube.data.model.entity.toDomain
import com.aube.domain.model.MyLottoNumbers
import com.aube.domain.repository.MyLottoNumbersRepository
import java.time.LocalDateTime
import javax.inject.Inject

class MyLottoNumbersRepositoryImpl @Inject constructor(
    private val dao: MyLottoNumbersDao
) : MyLottoNumbersRepository {

    override suspend fun saveMyNumbers(numbers: List<List<Int>>) {
        val entity = MyLottoNumbersEntity(
            numbers = numbers,
            date = LocalDateTime.now()
        )
        dao.insert(entity)
    }

    override suspend fun getMyNumbersHistory(): List<MyLottoNumbers> {
        return dao.getAll().map { it.toDomain() }
    }

    override suspend fun getMyNumbers(): MyLottoNumbers? {
        return dao.getLatest()?.toDomain()
    }

    override suspend fun deleteMyNumbers(id: Int) {
        dao.deleteById(id)
    }
}
