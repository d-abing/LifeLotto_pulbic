package com.aube.presentation.model

import com.aube.domain.model.MyLottoSet

data class MyLottoNumbersUiState(
    val beforeDraw: List<MyLottoSet> = emptyList(),
    val myNumbers: List<MyLottoSet> = emptyList(),
    val matchResult: MatchResult? = null,
    val matchHistory: List<Int> = emptyList()
)
