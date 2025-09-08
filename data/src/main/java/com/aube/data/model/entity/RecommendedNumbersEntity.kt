package com.aube.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aube.domain.model.LottoSet
import java.time.LocalDateTime

@Entity(
    tableName = "recommended_numbers",
    indices = [Index(value = ["numbers"], unique = true)]
)
data class RecommendedNumbersEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "numbers") val numbers: List<Int>,
    val date: LocalDateTime
)

fun RecommendedNumbersEntity.toDomain(): LottoSet {
    return LottoSet(
        id = id,
        numbers = numbers,
        date = date
    )
}

