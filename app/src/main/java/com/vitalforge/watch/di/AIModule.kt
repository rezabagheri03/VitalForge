package com.vitalforge.watch.di

import android.content.Context
import com.vitalforge.watch.data.ai.AnomalyDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AIModule {
    @Provides
    @Singleton
    fun provideAnomalyDetector(@ApplicationContext ctx: Context): AnomalyDetector =
        AnomalyDetector(ctx)
}
