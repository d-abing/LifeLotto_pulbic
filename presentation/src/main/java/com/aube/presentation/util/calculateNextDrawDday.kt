package com.aube.presentation.util

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime

fun calculateNextDrawDuration(): Duration {
    val now = LocalDateTime.now()
    val nextDrawDateTime = now.with(DayOfWeek.SATURDAY).withHour(20).withMinute(45).withSecond(0)

    val target = if (now > nextDrawDateTime) nextDrawDateTime.plusWeeks(1) else nextDrawDateTime
    return Duration.between(now, target)
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
