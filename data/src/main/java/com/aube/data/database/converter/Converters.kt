package com.aube.data.database.converter

import androidx.room.TypeConverter
import java.time.LocalDateTime

class LifeLottoTypeConverters {
    @TypeConverter
    fun fromList(list: List<Int>): String =
        list.joinToString("|")

    @TypeConverter
    fun toList(data: String): List<Int> =
        if (data.isBlank()) emptyList()
        else data.split("|").mapNotNull { it.toIntOrNull() }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String =
        dateTime.toString() // "2025-09-16T20:45:00"

    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime =
        runCatching { LocalDateTime.parse(value) }
            .getOrDefault(LocalDateTime.now())
}