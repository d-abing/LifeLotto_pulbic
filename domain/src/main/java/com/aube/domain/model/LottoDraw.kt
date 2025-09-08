package com.aube.domain.model

import java.time.LocalDateTime

data class LottoDraw(
    val round: Int,
    val numbers: List<Int>,
    val date: LocalDateTime
)
