package com.aube.domain.util

import java.time.Clock
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

private val KST: ZoneId = ZoneId.of("Asia/Seoul")
private val DRAW_DAY: DayOfWeek = DayOfWeek.SATURDAY
private val FIRST_DRAW_DATE: LocalDate = LocalDate.of(2002, 12, 7)
private val DRAW_TIME: LocalTime = LocalTime.of(20, 45)

fun estimateLatestRound(
    nowDate: LocalDate = LocalDate.now(KST)
): Int {
    val weeks = ChronoUnit.WEEKS.between(FIRST_DRAW_DATE, nowDate)
    return weeks.toInt() + 1
}

fun estimateLatestDateTime(
    nowClock: Clock = Clock.system(KST)
): LocalDateTime {
    val nowDate = LocalDate.now(nowClock)
    val latestRound = estimateLatestRound(nowDate)
    val latestDate = FIRST_DRAW_DATE.plusWeeks((latestRound - 1).toLong())
    return LocalDateTime.of(latestDate, DRAW_TIME)
}

fun nextDrawInstant(now: Instant = Instant.now()): Instant {
    val zNow = now.atZone(KST)
    val date = zNow.toLocalDate()
    val sat = date.with(java.time.temporal.TemporalAdjusters.nextOrSame(DRAW_DAY))
    val drawDateTime = LocalDateTime.of(sat, DRAW_TIME)
    val drawZdt = drawDateTime.atZone(KST)
    return if (!zNow.toInstant().isAfter(drawZdt.toInstant())) drawZdt.toInstant()
    else drawDateTime.plusWeeks(1).atZone(KST).toInstant()
}

fun roundForDrawInstant(instant: Instant): Int {
    val first = FIRST_DRAW_DATE.atTime(DRAW_TIME).atZone(KST).toInstant()
    val weeks = java.time.Duration.between(first, instant).toDays() / 7
    return weeks.toInt() + 1
}

