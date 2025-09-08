package com.aube.presentation.model

data class MyLottoNumbersUiState(
    val myNumbers: List<List<Int>>? = emptyList(),
    val matchResult: MatchResult? = MatchResult.BeforeDraw
)
