package com.aube.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aube.data.database.dao.LottoDrawDao
import com.aube.data.database.dao.MyLottoDao
import com.aube.data.database.dao.RecommendDao
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

    private const val DB_NAME = "life_lotto.db"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LifeLottoDatabase {
        return Room.databaseBuilder(context, LifeLottoDatabase::class.java, DB_NAME)
            // .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .build()
    }

    @Provides fun provideMyLottoDao(db: LifeLottoDatabase): MyLottoDao = db.myLottoDao()
    @Provides fun provideRecommendDao(db: LifeLottoDatabase): RecommendDao = db.recommendDao()
    @Provides fun provideLottoDrawDao(db: LifeLottoDatabase): LottoDrawDao = db.lottoDrawDao()
}

