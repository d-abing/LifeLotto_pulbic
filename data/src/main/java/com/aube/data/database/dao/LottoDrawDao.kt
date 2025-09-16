package com.aube.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aube.data.model.entity.LottoDrawEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LottoDrawDao {
    @Query("SELECT * FROM lotto_draw ORDER BY round DESC")
    fun observeAll(): Flow<List<LottoDrawEntity>>

    @Query("SELECT MAX(round) FROM lotto_draw")
    suspend fun getLatestRound(): Int?

    @Upsert
    suspend fun upsert(entity: LottoDrawEntity)
}