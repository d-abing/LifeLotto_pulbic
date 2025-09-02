package com.aube.data.database.converter

import androidx.room.TypeConverter
import java.time.LocalDateTime

class LifeLottoTypeConverters {

    @TypeConverter
    fun fromNestedList(list: List<List<Int>>): String =
        list.joinToString("|") { it.joinToString(",") }

    @TypeConverter
    fun toNestedList(data: String): List<List<Int>> =
        if (data.isBlank()) emptyList()
        else data.split("|").map { it.split(",").map(String::toInt) }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String =
        dateTime.toString()

    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime =
        LocalDateTime.parse(value)
}