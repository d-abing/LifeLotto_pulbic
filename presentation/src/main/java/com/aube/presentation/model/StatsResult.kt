package com.aube.presentation.model

data class StatsResult(
    val freq: IntArray,                     // index=번호, 값=출현 횟수 (1..45 사용)
    val maxCount: Int,
    val top8: List<Pair<Int, Int>>,         // (번호, 횟수)
    val low8: List<Pair<Int, Int>>
)