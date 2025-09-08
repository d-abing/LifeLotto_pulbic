package com.aube.presentation.model

data class FortuneUiState(
    val isLoading: Boolean = true,
    val fortune: Fortune? = null
)