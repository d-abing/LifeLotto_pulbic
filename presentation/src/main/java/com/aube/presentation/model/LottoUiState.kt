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
)



fun LottoResult.toUiState(): LottoUiState {
    val formattedPrize = NumberFormat.getNumberInstance(Locale.KOREA).format(firstPrize) + "원"

    return LottoUiState(
        round = round,
        date = date,
        winningNumbers = winningNumbers,
        bonus = bonus,
        firstPrize = formattedPrize,
        firstCount = firstCount,
    )
}
