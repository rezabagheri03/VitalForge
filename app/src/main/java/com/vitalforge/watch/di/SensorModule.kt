package com.vitalforge.watch.di

import android.content.Context
import com.vitalforge.watch.data.sensor.SamsungHealthSensorManager
import com.vitalforge.watch.data.health.HealthServicesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SensorModule {
    @Provides
    @Singleton
    fun provideSamsungHealthSensorManager(@ApplicationContext ctx: Context): SamsungHealthSensorManager =
        SamsungHealthSensorManager(ctx)

    @Provides
    @Singleton
    fun provideHealthServicesManager(@ApplicationContext ctx: Context): HealthServicesManager =
        HealthServicesManager(ctx)
}
