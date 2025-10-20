package com.aube.data.repository

import com.aube.data.database.dao.RecommendDao
import com.aube.data.model.entity.RecommendedNumbersEntity
import com.aube.data.model.entity.toDomain
import com.aube.domain.model.LottoSet
import com.aube.domain.repository.RecommendRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class RecommendRepositoryImpl @Inject constructor(
    private val dao: RecommendDao
) : RecommendRepository {
    override fun getSavedNumbers(): Flow<List<LottoSet>> {
        return dao.getAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun save(numbers: List<Int>) {
        val entity = RecommendedNumbersEntity(
            numbers = numbers,
            date = LocalDateTime.now()
        )
        dao.insert(entity)
    }

    override suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}