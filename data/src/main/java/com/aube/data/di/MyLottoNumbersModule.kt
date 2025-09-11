package com.aube.data.di

import com.aube.data.database.dao.MyLottoNumbersDao
import com.aube.data.repository.MyLottoNumbersRepositoryImpl
import com.aube.data.retrofit.LottoApiService
import com.aube.domain.repository.MyLottoNumbersRepository
import com.aube.domain.usecase.DeleteMyLottoNumbersUseCase
import com.aube.domain.usecase.GetMyLottoNumbersHistoryUseCase
import com.aube.domain.usecase.GetMyLottoNumbersUseCase
import com.aube.domain.usecase.SaveMyLottoNumbersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MyLottoNumbersModule {

    @Provides
    @Singleton
    fun provideMyLottoNumbersRepository(
        api: LottoApiService,
        dao: MyLottoNumbersDao
    ): MyLottoNumbersRepository {
        return MyLottoNumbersRepositoryImpl(api, dao)
    }

    @Provides
    fun provideSaveMyLottoNumbersUseCase(
        repo: MyLottoNumbersRepository
    ) = SaveMyLottoNumbersUseCase(repo)

    @Provides
    fun provideGetMyLottoNumbersUseCase(
        repo: MyLottoNumbersRepository
    ) = GetMyLottoNumbersUseCase(repo)

    @Provides
    fun provideDeleteMyLottoNumbersUseCase(
        repo: MyLottoNumbersRepository
    ) = DeleteMyLottoNumbersUseCase(repo)

    @Provides
    fun provideGetMyLottoNumbersHistoryUseCase(
        repo: MyLottoNumbersRepository
    ) = GetMyLottoNumbersHistoryUseCase(repo)
}
