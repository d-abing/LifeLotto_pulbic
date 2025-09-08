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
        else data.split("|").map(String::toInt)

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String =
        dateTime.toString()

    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime =
        LocalDateTime.parse(value)
}