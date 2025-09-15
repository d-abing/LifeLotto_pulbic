package com.aube.presentation.util.qrcode

data class QrParsed(
    val round: Int,
    val sets: List<List<Int>> // A~E 등 여러 줄
)