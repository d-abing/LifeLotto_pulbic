package com.aube.data.di

import com.aube.data.database.dao.MyLottoDao
import com.aube.data.repository.MyLottoRepositoryImpl
import com.aube.data.retrofit.LottoApiService
import com.aube.domain.repository.MyLottoRepository
import com.aube.domain.usecase.DeleteAllBeforeDrawMyLottoNumbersUseCase
import com.aube.domain.usecase.DeleteMyLottoNumberUseCase
import com.aube.domain.usecase.GetBeforeDrawMyLottoNumbersUseCase
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
object MyLottoModule {

    @Provides
    @Singleton
    fun provideMyLottoRepository(
        api: LottoApiService,
        dao: MyLottoDao
    ): MyLottoRepository {
        return MyLottoRepositoryImpl(api, dao)
    }

    @Provides fun provideSaveMyLottoNumbersUseCase(repo: MyLottoRepository) = SaveMyLottoNumbersUseCase(repo)
    @Provides fun provideGetMyLottoNumbersUseCase(repo: MyLottoRepository) = GetMyLottoNumbersUseCase(repo)
    @Provides fun provideGetBeforeDrawMyLottoNumbersUseCase(repo: MyLottoRepository) = GetBeforeDrawMyLottoNumbersUseCase(repo)
    @Provides fun provideDeleteMyLottoNumbersUseCase(repo: MyLottoRepository) = DeleteMyLottoNumberUseCase(repo)
    @Provides fun provideGetMyLottoNumbersHistoryUseCase(repo: MyLottoRepository) = GetMyLottoNumbersHistoryUseCase(repo)
    @Provides fun provideDeleteAllMyLottoNumbersUseCase(repo: MyLottoRepository) = DeleteAllBeforeDrawMyLottoNumbersUseCase(repo)
}
