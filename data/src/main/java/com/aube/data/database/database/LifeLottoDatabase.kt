package com.aube.data.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aube.data.database.converter.LifeLottoTypeConverters
import com.aube.data.database.dao.MyLottoNumbersDao
import com.aube.data.model.entity.MyLottoNumbersEntity

@Database(
    entities = [MyLottoNumbersEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LifeLottoTypeConverters::class)
abstract class LifeLottoDatabase : RoomDatabase() {
    abstract fun myLottoNumbersDao(): MyLottoNumbersDao
}
