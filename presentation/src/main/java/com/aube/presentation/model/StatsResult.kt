package com.aube.presentation.model

data class StatsResult(
    val freq: List<Int>,
    val maxCount: Int,
    val top8: List<Pair<Int, Int>>,
    val low8: List<Pair<Int, Int>>
)