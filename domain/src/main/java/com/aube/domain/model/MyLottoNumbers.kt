package com.aube.domain.model

import java.time.LocalDateTime

data class MyLottoNumbers(
    val id: Int,
    val numbers: List<List<Int>>,
    val date: LocalDateTime
)
