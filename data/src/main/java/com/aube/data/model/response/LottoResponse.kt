package com.aube.data.model.response

import androidx.annotation.Keep
import com.aube.domain.model.LottoResult
import com.google.gson.annotations.SerializedName

@Keep
data class LottoResponse(
    @SerializedName("returnValue") val returnValue: String?,
    @SerializedName("drwNo")       val round: Int?,
    @SerializedName("drwNoDate")   val date: String?,
    @SerializedName("drwtNo1")     val no1: Int?,
    @SerializedName("drwtNo2")     val no2: Int?,
    @SerializedName("drwtNo3")     val no3: Int?,
    @SerializedName("drwtNo4")     val no4: Int?,
    @SerializedName("drwtNo5")     val no5: Int?,
    @SerializedName("drwtNo6")     val no6: Int?,
    @SerializedName("bnusNo")      val bonus: Int?,
    @SerializedName("firstWinamnt") val firstPrize: Long?,
    @SerializedName("firstPrzwnerCo") val firstCount: Int?
)

fun LottoResponse.toDomainOrNull(): LottoResult? {
    if (returnValue != null && returnValue != "success") return null

    val r = round ?: return null
    val dateStr = date?.takeIf { it.isNotBlank() } ?: return null

    val numsNullable = listOf(no1, no2, no3, no4, no5, no6)
    if (numsNullable.any { it == null }) return null
    val nums = numsNullable.filterNotNull()

    val b = bonus ?: return null

    val prize = firstPrize ?: 0L
    val count = firstCount ?: 0

    return LottoResult(
        round = r,
        date = dateStr,             // 도메인이 String이면 그대로. LocalDate로 파싱하려면 여기서 시도.
        winningNumbers = nums,
        bonus = b,
        firstPrize = prize,
        firstCount = count
    )
}