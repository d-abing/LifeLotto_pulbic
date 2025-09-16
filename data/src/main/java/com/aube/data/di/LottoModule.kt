package com.aube.data.di

import com.aube.data.BuildConfig
import com.aube.data.database.dao.LottoDrawDao
import com.aube.data.repository.LottoRepositoryImpl
import com.aube.data.retrofit.LottoApiService
import com.aube.domain.repository.LottoRepository
import com.aube.domain.usecase.GetLottoResultUseCase
import com.aube.domain.usecase.ObserveDrawHistoryUseCase
import com.aube.domain.usecase.SyncDrawHistoryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LottoModule {

    @Provides
    @Singleton
    fun provideLottoApi(): LottoApiService {
        val okHttp = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .apply {
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                    addInterceptor(logging)
                }
            }
            .retryOnConnectionFailure(true)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://www.dhlottery.co.kr/")
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LottoApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLottoRepository(
        api: LottoApiService,
        dao: LottoDrawDao
    ): LottoRepository = LottoRepositoryImpl(api, dao)

    @Provides fun provideGetLottoResultUseCase(repo: LottoRepository) = GetLottoResultUseCase(repo)
    @Provides fun provideObserveDrawHistoryUseCase(repo: LottoRepository) = ObserveDrawHistoryUseCase(repo)
    @Provides fun provideSyncDrawHistoryUseCase(repo: LottoRepository) = SyncDrawHistoryUseCase(repo)
}