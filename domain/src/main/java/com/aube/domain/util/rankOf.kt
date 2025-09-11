package com.aube.domain.util

fun rankOf(
    my: List<Int>,
    winning: List<Int>,
    bonus: Int
): Int? {
    val match = winning.count { it in my }
    val hasBonus = bonus in my
    return when {
        match == 6 -> 1
        match == 5 && hasBonus -> 2
        match == 5 -> 3
        match == 4 -> 4
        match == 3 -> 5
        else -> null
    }
}