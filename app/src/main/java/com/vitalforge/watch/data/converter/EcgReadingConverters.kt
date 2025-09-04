package com.vitalforge.watch.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vitalforge.watch.data.model.*

class EcgReadingConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromFloatArray(array: FloatArray): String =
        gson.toJson(array)

    @TypeConverter
    fun toFloatArray(json: String): FloatArray =
        gson.fromJson(json, FloatArray::class.java)

    @TypeConverter
    fun fromLeadType(type: EcgLeadType): String = type.name

    @TypeConverter
    fun toLeadType(name: String): EcgLeadType = EcgLeadType.valueOf(name)

    @TypeConverter
    fun fromRhythm(rhythm: CardiacRhythm): String = rhythm.name

    @TypeConverter
    fun toRhythm(name: String): CardiacRhythm = CardiacRhythm.valueOf(name)

    @TypeConverter
    fun fromQrsList(list: List<QrsComplex>): String =
        gson.toJson(list)

    @TypeConverter
    fun toQrsList(json: String): List<QrsComplex> {
        val type = object : TypeToken<List<QrsComplex>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromFlags(list: List<AnalysisFlag>): String =
        gson.toJson(list)

    @TypeConverter
    fun toFlags(json: String): List<AnalysisFlag> {
        val type = object : TypeToken<List<AnalysisFlag>>() {}.type
        return gson.fromJson(json, type)
    }
}
