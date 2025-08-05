package com.aube.presentation.model

data class LottoUiState(
    val round: Int,
    val date: String,
    val winningNumbers: List<Int>,
    val bonusNumber: Int,
    val totalPrize: String,
    val winnerCount: Int,
    val myNumbers: List<Int>? = null,
    val matchResult: MatchResult = MatchResult.BeforeDraw
) {
    companion object {
        fun mock() = LottoUiState(
            round = 1183,
            date = "2025-08-02",
            winningNumbers = listOf(4, 15, 17, 23, 27, 36),
            bonusNumber = 31,
            totalPrize = "270억 원",
            winnerCount = 13,
            myNumbers = listOf(4, 15, 23, 27, 30, 36),
            matchResult = MatchResult.Win(rank = 5, prize = "5만 원")
        )
    }
}
