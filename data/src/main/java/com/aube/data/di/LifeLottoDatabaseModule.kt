package com.aube.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
            .addMigrations(MIGRATION_1_2)
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .build()
    }

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // 인덱스 재정의
            // 기존 인덱스 삭제 후 새 복합 인덱스 생성
            database.execSQL("DROP INDEX IF EXISTS index_my_lotto_numbers_numbers")
            database.execSQL(
                """
            CREATE UNIQUE INDEX IF NOT EXISTS index_my_lotto_numbers_numbers_round
            ON my_lotto_numbers(numbers, round)
            """.trimIndent()
            )
        }
    }

    @Provides fun provideMyLottoDao(db: LifeLottoDatabase): MyLottoDao = db.myLottoDao()
    @Provides fun provideRecommendDao(db: LifeLottoDatabase): RecommendDao = db.recommendDao()
    @Provides fun provideLottoDrawDao(db: LifeLottoDatabase): LottoDrawDao = db.lottoDrawDao()
}

