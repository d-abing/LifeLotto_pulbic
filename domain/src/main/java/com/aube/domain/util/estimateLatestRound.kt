package com.aube.domain.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

fun estimateLatestRound(): Int {
    val firstDrawDate = LocalDate.of(2002, 12, 7)
    val today = LocalDate.now()
    val weeks = ChronoUnit.WEEKS.between(firstDrawDate, today)
    return weeks.toInt() + 1
}

fun estimateLatestDateTime(): LocalDateTime {
    val baseDate = LocalDate.of(2002, 12, 7) // 1회차 추첨일
    val drawTime = LocalTime.of(20, 45)      // 추첨 시간
    val latestRound = estimateLatestRound()  // 이미 구현된 함수
    val latestDate = baseDate.plusWeeks((latestRound - 1).toLong())
    return LocalDateTime.of(latestDate, drawTime)
}