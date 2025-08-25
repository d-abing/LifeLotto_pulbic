package com.aube.presentation.model

data class Fortune(
    val dateEpochDay: Long,
    val score: Int,
    val summary: String,
    val luckyNumbers: List<Int>,
    val luckyTime: String,
)