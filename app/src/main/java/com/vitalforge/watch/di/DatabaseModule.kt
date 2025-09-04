package com.vitalforge.watch.di

import android.content.Context
import com.vitalforge.watch.data.database.DatabaseBuilder
import com.vitalforge.watch.data.database.VitalForgeDatabase
import com.vitalforge.watch.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): VitalForgeDatabase =
        DatabaseBuilder.getDatabase(ctx)

    @Provides fun provideVitalReadingDao(db: VitalForgeDatabase): VitalReadingDao =
        db.vitalReadingDao()
    @Provides fun provideSleepSessionDao(db: VitalForgeDatabase): SleepSessionDao =
        db.sleepSessionDao()
    @Provides fun provideEcgReadingDao(db: VitalForgeDatabase): EcgReadingDao =
        db.ecgReadingDao()
    @Provides fun provideAiInsightDao(db: VitalForgeDatabase): AiInsightDao =
        db.aiInsightDao()
    @Provides fun provideUserPreferenceDao(db: VitalForgeDatabase): UserPreferenceDao =
        db.userPreferenceDao()
    @Provides fun provideConsentDao(db: VitalForgeDatabase): ConsentDao =
        db.consentDao()
}
