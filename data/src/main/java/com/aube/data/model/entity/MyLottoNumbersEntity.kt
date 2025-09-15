package com.aube.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aube.domain.model.MyLottoSet
import java.time.LocalDateTime

@Entity(
    tableName = "my_lotto_numbers",
    indices = [Index(value = ["numbers"], unique = true)]
)
data class MyLottoNumbersEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "numbers") val numbers: List<Int>,
    val round: Int,
    val date: LocalDateTime,
    val rank: Int? = null
)

fun MyLottoNumbersEntity.toDomain(): MyLottoSet {
    return MyLottoSet(
        id = id,
        numbers = numbers,
        round = round,
        date = date,
        rank = rank
    )
}