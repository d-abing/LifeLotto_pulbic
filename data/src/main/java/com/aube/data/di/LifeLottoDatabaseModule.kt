package com.aube.data.di

import android.content.Context
import androidx.room.Room
import com.aube.data.database.dao.MyLottoNumbersDao
import com.aube.data.database.database.LifeLottoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LifeLottoDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LifeLottoDatabase {
        return Room.databaseBuilder(
            context,
            LifeLottoDatabase::class.java,
            "life_lotto_database"
        ).build()
    }

    @Provides
    fun provideMyLottoNumbersDao(database: LifeLottoDatabase): MyLottoNumbersDao {
        return database.myLottoNumbersDao()
    }
}
