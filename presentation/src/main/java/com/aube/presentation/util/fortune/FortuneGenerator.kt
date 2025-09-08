package com.aube.presentation.util.fortune

import com.aube.presentation.model.Fortune
import java.time.LocalDate
import kotlin.random.Random

object FortuneGenerator {
    private val summaries = listOf(
        "작은 기회가 큰 수익으로 이어져요.",
        "지출보다 수입에 집중하면 좋아요.",
        "사람을 통해 재물이 들어옵니다.",
        "모아온 경험이 수익 전환점을 만듭니다.",
        "의외의 절약이 큰 힘이 됩니다."
    )

    fun generate(date: LocalDate, seedKey: String = ""): Fortune {
        val rnd = Random((date.toEpochDay().toString() + seedKey).hashCode())
        val score = rnd.nextInt(40, 101)
        val nums = buildSet { while (size < 3) add(rnd.nextInt(1, 46)) }.toList().sorted()
        val hour = listOf("09:00","11:00","13:00","15:00","17:00","19:00")[rnd.nextInt(6)]
        return Fortune(
            dateEpochDay = date.toEpochDay(),
            score = score,
            summary = summaries[rnd.nextInt(summaries.size)],
            luckyNumbers = nums,
            luckyTime = "$hour - ${hour.replace(":00",":30")}",
        )
    }
}

