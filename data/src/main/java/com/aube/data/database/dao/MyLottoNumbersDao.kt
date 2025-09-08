package com.aube.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aube.data.model.entity.MyLottoNumbersEntity

@Dao
interface MyLottoNumbersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MyLottoNumbersEntity)

    @Query("SELECT * FROM my_lotto_numbers ORDER BY date DESC")
    suspend fun getAll(): List<MyLottoNumbersEntity>

    @Query("SELECT * FROM my_lotto_numbers WHERE date > :latestDate ORDER BY date")
    suspend fun getLatest(latestDate: String): List<MyLottoNumbersEntity>

    @Query("DELETE FROM my_lotto_numbers WHERE id = :id")
    suspend fun deleteById(id: Int)
}
