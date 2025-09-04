package com.vitalforge.watch.di

import android.content.Context
import com.vitalforge.watch.data.security.ConsentManager
import com.vitalforge.watch.data.security.EncryptionManager
import com.vitalforge.watch.data.dao.ConsentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    @Provides
    @Singleton
    fun provideEncryptionManager(@ApplicationContext ctx: Context): EncryptionManager =
        EncryptionManager(ctx)

    @Provides
    @Singleton
    fun provideConsentManager(consentDao: ConsentDao): ConsentManager =
        ConsentManager(consentDao)
}
