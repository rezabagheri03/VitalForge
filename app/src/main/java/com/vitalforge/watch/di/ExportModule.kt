package com.vitalforge.watch.di

import android.content.Context
import com.vitalforge.watch.data.export.DataExporter
import com.vitalforge.watch.data.export.PdfGenerator
import com.vitalforge.watch.data.security.EncryptionManager
import com.vitalforge.watch.data.security.ConsentManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExportModule {
    @Provides
    @Singleton
    fun providePdfGenerator(@ApplicationContext ctx: Context): PdfGenerator =
        PdfGenerator(ctx)

    @Provides
    @Singleton
    fun provideDataExporter(
        pdfGenerator: PdfGenerator,
        encryptionManager: EncryptionManager,
        consentManager: ConsentManager
    ): DataExporter = DataExporter(pdfGenerator, encryptionManager, consentManager)
}
