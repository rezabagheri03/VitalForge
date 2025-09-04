package com.vitalforge.watch.data.converter

import androidx.room.TypeConverter
import com.vitalforge.watch.data.model.DataAccuracy
import com.vitalforge.watch.data.model.DataSource

class VitalReadingConverters {
    @TypeConverter fun fromSource(source: DataSource): String = source.name
    @TypeConverter fun toSource(name: String): DataSource = DataSource.valueOf(name)
    @TypeConverter fun fromAccuracy(acc: DataAccuracy): String = acc.name
    @TypeConverter fun toAccuracy(name: String): DataAccuracy = DataAccuracy.valueOf(name)
}
