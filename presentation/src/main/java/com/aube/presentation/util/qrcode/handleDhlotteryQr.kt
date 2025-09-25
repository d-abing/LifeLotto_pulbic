package com.aube.presentation.util.qrcode

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent

private const val QR_TAG = "QR"

/** 붙여넣기/공유 과정에서 섞이는 제어문자/제로폭 공백 제거 */
private fun String.sanitizeUrl(): String =
    trim()
        .replace("\u200B", "")                       // zero-width space
        .replace(Regex("[\\u0000-\\u001F]"), "")     // 제어문자 (\n, \r, \t 등)

/** dhlottery 도메인인지 체크 */
private fun isDhlotteryHost(host: String?): Boolean =
    host?.endsWith("dhlottery.co.kr", ignoreCase = true) == true

/**
 * m.dhlottery.co.kr의 QR을 표준 URL로 정규화:
 * - 이미 /qr.do?method=winQr&v=... 이면 그대로
 * - /?v=... 처럼 오면 /qr.do?method=winQr&v=... 로 변환 (http -> https 승격)
 * - 그 외는 null
 */
private fun buildCanonicalWinQrUri(from: Uri): Uri? {
    if (!isDhlotteryHost(from.host)) return null

    val lastSeg = from.lastPathSegment
    val method = from.getQueryParameter("method")
    val v = from.getQueryParameter("v")

    return when {
        // 이미 표준 형태
        lastSeg.equals("qr.do", ignoreCase = true)
                && method.equals("winQr", ignoreCase = true)
                && !v.isNullOrBlank() -> from

        // 루트 "/?v=..." 같은 형태 → 표준으로 변환
        !lastSeg.equals("qr.do", ignoreCase = true) && !v.isNullOrBlank() -> Uri.Builder()
            .scheme("https")
            .authority("m.dhlottery.co.kr")
            .path("qr.do")
            .appendQueryParameter("method", "winQr")
            .appendQueryParameter("v", v)
            .build()

        else -> null
    }
}

/**
 * v 파라미터 직접 파싱.
 * 포맷: {round}q{A(12digits)}q{B(12)}q{C(12)}q{D(12)}q{E(12)}{tail...}
 * - 각 블록은 2자리×6개 = 12자리
 * - 뒤 꼬리 숫자는 무시
 */
private fun parseDhlotteryV(vRaw: String, sortNumbers: Boolean = true): QrParsed? {
    val v = vRaw.trim().replace("\u200B", "")
    val parts = v.split('q')
    if (parts.isEmpty()) return null

    val round = parts[0].toIntOrNull() ?: return null
    val sets = mutableListOf<List<Int>>()

    for (i in 1 until parts.size) {
        val blockDigits = parts[i].filter { it.isDigit() }
        if (blockDigits.length < 12) continue
        val twelve = blockDigits.substring(0, 12)

        val nums = (0 until 6).map { idx ->
            twelve.substring(idx * 2, idx * 2 + 2).toInt()
        }

        // 유효성 검사 (1..45)
        if (nums.size == 6 && nums.all { it in 1..45 }) {
            sets += if (sortNumbers) nums.sorted() else nums
        }
    }

    if (sets.isEmpty()) return null
    return QrParsed(round = round, sets = sets)
}

/** URI에서 v를 꺼내 parse */
private fun parseFromQrUri(uri: Uri, sortNumbers: Boolean = true): QrParsed? {
    if (!isDhlotteryHost(uri.host)) return null
    val v = uri.getQueryParameter("v") ?: return null
    return parseDhlotteryV(v, sortNumbers)
}

// ======================= 메인 함수 ======================
/**
 * 동행복권 QR URL 처리:
 * - 표준 URL로 정규화해 CustomTabs로 열어줌
 * - 동시에 v를 직접 파싱하여 (round, 여러 세트) 콜백으로 전달
 */
fun handleDhlotteryQr(
    context: Context,
    url: String,
    openInCustomTab: Boolean = true,
    sortNumbers: Boolean = true,
    onParsed: (round: Int, sets: List<List<Int>>) -> Unit = { _, _ -> }
) {
    val clean = url.sanitizeUrl()
    val raw = Uri.parse(clean)
    val canonical = buildCanonicalWinQrUri(raw)
    val openUri = canonical ?: raw

    // 결과 페이지 열기(원하면 끌 수 있음)
    if (openInCustomTab) {
        CustomTabsIntent.Builder().build().launchUrl(context, openUri)
    }

    // v 파라미터 기반으로 회차/세트들 파싱
    val parsed = parseFromQrUri(openUri, sortNumbers)
        ?: parseFromQrUri(raw, sortNumbers) // 혹시 canonical이 null이어도 대비
    if (parsed != null) {
        // Log.d(QR_TAG, "parsed: round=${parsed.round}, sets=${parsed.sets}")
        onParsed(parsed.round, parsed.sets)
    } else {
        // Log.d(QR_TAG, "parse failed (not a dhlottery QR or invalid v)")
    }
}