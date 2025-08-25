package com.aube.domain.model

data class LottoSet(
    val id: Long = 0L,
    val numbers: List<Int>,
    val createdAt: Long,
    val note: String? = null
)
