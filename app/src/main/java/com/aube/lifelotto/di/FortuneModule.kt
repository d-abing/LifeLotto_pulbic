package com.aube.lifelotto.di

import android.content.Context
import android.provider.Settings
import com.aube.presentation.util.fortune.SeedKeyProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FortuneModule {
    @Provides
    @Singleton
    fun provideSeedKey(@ApplicationContext ctx: Context): SeedKeyProvider =
        SeedKeyProvider { Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID) ?: "" }
}