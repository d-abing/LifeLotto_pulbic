package com.aube.data.di

import com.aube.data.database.dao.RecommendDao
import com.aube.data.repository.RecommendRepositoryImpl
import com.aube.domain.repository.RecommendRepository
import com.aube.domain.usecase.DeleteRecommendedNumbersUseCase
import com.aube.domain.usecase.GetRecommendedNumbersUseCase
import com.aube.domain.usecase.SaveRecommendedNumbersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RecommendModule {

    @Provides
    @Singleton
    fun provideRecommendRepository(
        dao: RecommendDao
    ): RecommendRepository {
        return RecommendRepositoryImpl(dao)
    }

    @Provides fun provideSaveRecommendedNumbersUseCase(repo: RecommendRepository) = SaveRecommendedNumbersUseCase(repo)
    @Provides fun provideGetRecommendedNumbersUseCase(repo: RecommendRepository) = GetRecommendedNumbersUseCase(repo)
    @Provides fun provideDeleteRecommendedNumbersUseCase(repo: RecommendRepository) = DeleteRecommendedNumbersUseCase(repo)
}
