package com.aube.domain.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

private val KST: ZoneId = ZoneId.of("Asia/Seoul")
private val DRAW_DAY: DayOfWeek = DayOfWeek.SATURDAY
private val DRAW_TIME: LocalTime = LocalTime.of(20, 45)

fun nextDrawInstant(now: Instant = Instant.now()): Instant {
    val zNow = now.atZone(KST)
    val date = zNow.toLocalDate()
    val sat = date.with(java.time.temporal.TemporalAdjusters.nextOrSame(DRAW_DAY))
    val drawDateTime = LocalDateTime.of(sat, DRAW_TIME)
    val drawZdt = drawDateTime.atZone(KST)
    return if (zNow.toInstant().isBefore(drawZdt.toInstant())) drawZdt.toInstant()
    else drawDateTime.plusWeeks(1).atZone(KST).toInstant()
}

fun roundForDrawInstant(instant: Instant): Int {
    val first = LocalDate.of(2002, 12, 7).atTime(DRAW_TIME).atZone(KST).toInstant()
    val weeks = java.time.Duration.between(first, instant).toDays() / 7
    return weeks.toInt() + 1
}

interface NotificationScheduler {
    fun enable()
    fun disable()
}