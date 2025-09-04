package com.vitalforge.watch.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vitalforge.watch.data.model.*

class SleepSessionConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromSleepStages(stages: List<SleepStage>): String =
        gson.toJson(stages)

    @TypeConverter
    fun toSleepStages(json: String): List<SleepStage> {
        val type = object : TypeToken<List<SleepStage>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromAwakeIntervals(list: List<AwakeInterval>): String =
        gson.toJson(list)

    @TypeConverter
    fun toAwakeIntervals(json: String): List<AwakeInterval> {
        val type = object : TypeToken<List<AwakeInterval>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromFloatList(list: List<Float>): String =
        gson.toJson(list)

    @TypeConverter
    fun toFloatList(json: String): List<Float> {
        val type = object : TypeToken<List<Float>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromEnvData(data: EnvironmentalData?): String? =
        data?.let { gson.toJson(it) }

    @TypeConverter
    fun toEnvData(json: String?): EnvironmentalData? =
        json?.let { gson.fromJson(it, EnvironmentalData::class.java) }
}
