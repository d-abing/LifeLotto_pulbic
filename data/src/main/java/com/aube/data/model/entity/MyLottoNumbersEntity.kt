package com.aube.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aube.domain.model.MyLottoNumbers
import java.time.LocalDateTime

@Entity(tableName = "my_lotto_numbers")
data class MyLottoNumbersEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val numbers: List<List<Int>>,
    val date: LocalDateTime
)

fun MyLottoNumbersEntity.toDomain(): MyLottoNumbers {
    return MyLottoNumbers(
        id = id,
        numbers = numbers,
        date = date
    )
}
