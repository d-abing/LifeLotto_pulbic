package com.aube.data.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aube.data.database.converter.LifeLottoTypeConverters
import com.aube.data.database.dao.LottoDrawDao
import com.aube.data.database.dao.MyLottoNumbersDao
import com.aube.data.database.dao.RecommendDao
import com.aube.data.model.entity.LottoDrawEntity
import com.aube.data.model.entity.MyLottoNumbersEntity
import com.aube.data.model.entity.RecommendedNumbersEntity

@Database(
    entities = [MyLottoNumbersEntity::class, RecommendedNumbersEntity::class, LottoDrawEntity::class],
    version = 8,
    exportSchema = false
)
@TypeConverters(LifeLottoTypeConverters::class)
abstract class LifeLottoDatabase : RoomDatabase() {
    abstract fun myLottoNumbersDao(): MyLottoNumbersDao
    abstract fun recommendDao(): RecommendDao
    abstract fun lottoDrawDao(): LottoDrawDao
}
