package com.aube.domain.model

import java.time.LocalDateTime

data class LottoSet(
    val id: Int,
    val round: Int? = null,
    val numbers: List<Int>,
    val date: LocalDateTime
)
