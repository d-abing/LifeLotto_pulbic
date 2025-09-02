package com.aube.presentation.model

import com.aube.domain.model.LottoResult
import java.text.NumberFormat
import java.util.Locale

data class LottoUiState(
    val round: Int = 0,
    val date: String = "",
    val winningNumbers: List<Int> = emptyList(),
    val bonus: Int = 0,
    val firstPrize: String = "",
    val firstCount: Int = 0,
    val myNumbers: List<List<Int>>? = null,
    val matchResult: MatchResult = MatchResult.BeforeDraw
)



fun LottoResult.toUiState(myNumbers: List<List<Int>>? = null): LottoUiState {
    val matchResult = myNumbers?.let { allNumbers ->
        if (winningNumbers.isEmpty()) {
            MatchResult.BeforeDraw
        } else {
            val results = allNumbers.map { numbers ->
                val matchCount = winningNumbers.count { it in numbers }
                val hasBonus = bonus in numbers

                when {
                    matchCount == 6 -> 1
                    matchCount == 5 && hasBonus -> 2
                    matchCount == 5 -> 3
                    matchCount == 4 -> 4
                    matchCount == 3 -> 5
                    else -> null
                }
            }.filterNotNull()

            if (results.isEmpty()) {
                MatchResult.Lose
            } else {
                val bestRank = results.min()
                MatchResult.Win(rank = bestRank)
            }
        }
    } ?: MatchResult.BeforeDraw

    val formattedPrize = NumberFormat.getNumberInstance(Locale.KOREA).format(firstPrize) + "원"

    return LottoUiState(
        round = round,
        date = date,
        winningNumbers = winningNumbers,
        bonus = bonus,
        firstPrize = formattedPrize,
        firstCount = firstCount,
        myNumbers = myNumbers,
        matchResult = matchResult
    )
}
