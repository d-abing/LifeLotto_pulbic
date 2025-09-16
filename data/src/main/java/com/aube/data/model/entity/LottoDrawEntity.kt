package com.aube.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aube.domain.model.LottoDraw
import java.time.LocalDateTime

@Entity(tableName = "lotto_draw")
data class LottoDrawEntity(
    @PrimaryKey val round: Int,
    val numbers: List<Int>,
    val date: LocalDateTime,
)

fun LottoDrawEntity.toDomain(): LottoDraw {
    return LottoDraw(
        round = round,
        numbers = numbers,
        date = date
    )
}

fun LottoDraw.toEntity(): LottoDrawEntity {
    return LottoDrawEntity(
        round = round,
        numbers = numbers,
        date = date
    )
}