package com.aube.presentation.util

import java.time.LocalDateTime
import java.time.DayOfWeek
import java.time.Duration
import java.time.temporal.TemporalAdjusters

fun calculateNextDrawDday(now: LocalDateTime = LocalDateTime.now()): Duration {
    val drawDay = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
        .withHour(20).withMinute(45).withSecond(0).withNano(0)

    return Duration.between(now, drawDay)
}

fun formatDday(duration: Duration): String {
    val days = duration.toDays()
    val hours = duration.minusDays(days).toHours()
    val minutes = duration.minusDays(days).minusHours(hours).toMinutes()
    val seconds = duration.minusDays(days).minusHours(hours).minusMinutes(minutes).seconds
    return "D-${days}일 ${hours}시간 ${minutes}분 ${seconds}초 남음"
}

fun generateLottoNumbers(): List<Int> {
    val numbers = (1..45).shuffled().take(6).sorted()
    return numbers
}
