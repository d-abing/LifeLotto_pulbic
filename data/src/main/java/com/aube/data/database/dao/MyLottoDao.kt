package com.aube.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aube.data.model.entity.MyLottoNumbersEntity

@Dao
interface MyLottoDao {
    @Upsert
    suspend fun insert(entity: MyLottoNumbersEntity)

    @Query("SELECT * FROM my_lotto_numbers ORDER BY date DESC")
    suspend fun getAll(): List<MyLottoNumbersEntity>

    @Query("SELECT * FROM my_lotto_numbers WHERE round = :round ORDER BY date")
    suspend fun getBeforeDraw(round: Int): List<MyLottoNumbersEntity>

    @Query("DELETE FROM my_lotto_numbers WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE my_lotto_numbers SET rank = :rank WHERE id = :id")
    suspend fun updateRank(id: Int, rank: Int?)
}