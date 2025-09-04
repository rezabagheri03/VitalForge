package com.vitalforge.watch.di

import android.content.Context
import com.vitalforge.watch.data.ble.GattServer
import com.vitalforge.watch.data.ble.BluetoothSyncService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideGattServer(@ApplicationContext ctx: Context): GattServer =
        GattServer(ctx)

    @Provides
    @Singleton
    fun provideBluetoothSyncService(@ApplicationContext ctx: Context, gattServer: GattServer): BluetoothSyncService =
        BluetoothSyncService(ctx, gattServer)
}
