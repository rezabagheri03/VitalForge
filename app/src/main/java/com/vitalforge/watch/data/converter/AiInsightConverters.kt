package com.vitalforge.watch.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vitalforge.watch.data.model.*

class AiInsightConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromInsightType(type: InsightType): String = type.name

    @TypeConverter
    fun toInsightType(name: String): InsightType = InsightType.valueOf(name)

    @TypeConverter
    fun fromSeverity(severity: InsightSeverity): String = severity.name

    @TypeConverter
    fun toSeverity(name: String): InsightSeverity = InsightSeverity.valueOf(name)

    @TypeConverter
    fun fromStringList(list: List<String>): String =
        gson.toJson(list)

    @TypeConverter
    fun toStringList(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromUserFeedback(feedback: UserFeedback?): String? =
        feedback?.let { gson.toJson(it) }

    @TypeConverter
    fun toUserFeedback(json: String?): UserFeedback? =
        json?.let { gson.fromJson(it, UserFeedback::class.java) }

    @TypeConverter
    fun fromMetadata(map: Map<String, String>): String =
        gson.toJson(map)

    @TypeConverter
    fun toMetadata(json: String): Map<String, String> {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(json, type)
    }
}
