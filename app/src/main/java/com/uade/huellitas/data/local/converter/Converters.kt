package com.uade.huellitas.data.local.converter

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(value: List<String>): String = value.joinToString("|")

    @TypeConverter
    fun toList(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split("|")
}
