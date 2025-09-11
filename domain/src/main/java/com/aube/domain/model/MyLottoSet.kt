package com.aube.domain.model

import java.time.LocalDateTime

data class MyLottoSet(
    val id: Int,
    val numbers: List<Int>,
    val round: Int,
    val date: LocalDateTime,
    val rank: Int?
)
