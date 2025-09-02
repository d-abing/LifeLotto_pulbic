package com.aube.data.di

import com.aube.data.repository.LottoRepositoryImpl
import com.aube.data.retrofit.LottoApiService
import com.aube.domain.repository.LottoRepository
import com.aube.domain.usecase.GetLottoResultUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object LottoModule {

    @Provides
    fun provideLottoApi(): LottoApiService {
        return Retrofit.Builder()
            .baseUrl("https://www.dhlottery.co.kr/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LottoApiService::class.java)
    }

    @Provides
    fun provideLottoRepository(api: LottoApiService): LottoRepository {
        return LottoRepositoryImpl(api)
    }

    @Provides
    fun provideGetLottoResultUseCase(repo: LottoRepository): GetLottoResultUseCase {
        return GetLottoResultUseCase(repo)
    }
}
