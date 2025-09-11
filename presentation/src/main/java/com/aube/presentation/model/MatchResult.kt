package com.aube.presentation.model

sealed interface MatchResult {
    object Lose : MatchResult
    data class Win(val rank: Int) : MatchResult
}
