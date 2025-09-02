package com.aube.domain.model

data class LottoResult(
    val round: Int,
    val date: String,
    val winningNumbers: List<Int>,
    val bonus: Int,
    val firstPrize: Long,
    val firstCount: Int
)
