package com.aube.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aube.data.model.entity.LottoDrawEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LottoDrawDao {
    @Query("SELECT * FROM lotto_draw ORDER BY round DESC")
    fun observeAll(): Flow<List<LottoDrawEntity>>

    @Query("SELECT MAX(round) FROM lotto_draw")
    suspend fun getLatestRound(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: LottoDrawEntity)
}