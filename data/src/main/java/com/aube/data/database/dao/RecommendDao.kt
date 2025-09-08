package com.aube.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aube.data.model.entity.RecommendedNumbersEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: RecommendedNumbersEntity)

    @Query("SELECT * FROM recommended_numbers ORDER BY date")
    fun getAll(): Flow<List<RecommendedNumbersEntity>>

    @Query("DELETE FROM recommended_numbers WHERE id = :id")
    suspend fun deleteById(id: Int)
}
