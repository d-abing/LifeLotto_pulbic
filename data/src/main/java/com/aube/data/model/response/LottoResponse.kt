package com.aube.data.model.response

import androidx.annotation.Keep
import com.aube.domain.model.LottoResult
import com.google.gson.annotations.SerializedName

@Keep
data class LottoResponse(
    @SerializedName("drwNo") val round: Int,
    @SerializedName("drwNoDate") val date: String,
    @SerializedName("drwtNo1") val no1: Int,
    @SerializedName("drwtNo2") val no2: Int,
    @SerializedName("drwtNo3") val no3: Int,
    @SerializedName("drwtNo4") val no4: Int,
    @SerializedName("drwtNo5") val no5: Int,
    @SerializedName("drwtNo6") val no6: Int,
    @SerializedName("bnusNo") val bonus: Int,
    @SerializedName("firstWinamnt") val firstPrize: Long,
    @SerializedName("firstPrzwnerCo") val firstCount: Int
)

fun LottoResponse.toDomain(): LottoResult {
    return LottoResult(
        round = round,
        date = date,
        winningNumbers = listOf(no1, no2, no3, no4, no5, no6),
        bonus = bonus,
        firstPrize = firstPrize,
        firstCount = firstCount
    )
}
