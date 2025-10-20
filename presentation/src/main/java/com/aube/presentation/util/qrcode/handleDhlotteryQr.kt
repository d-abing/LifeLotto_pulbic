import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import com.aube.presentation.util.qrcode.QrParsed

private const val QR_TAG = "QR"

// 제로폭/제어문자 제거용
private val ZERO_WIDTH = Regex("[\\uFEFF\\u200B-\\u200D\\u2060]")
private val CTRL = Regex("[\\u0000-\\u001F]")

/** 붙여넣기/공유 과정에서 섞이는 제어문자/제로폭 공백 제거 + 스킴 보정 */
private fun String.sanitizeUrl(): String =
    trim()
        .replace(ZERO_WIDTH, "")
        .replace(CTRL, "")
        .let { if (it.startsWith("//")) "https:$it" else it } // 스킴 없는 형태 보정

/** dhlottery 도메인인지 체크 */
private fun isDhlotteryHost(host: String?): Boolean =
    host?.endsWith("dhlottery.co.kr", ignoreCase = true) == true

/** v 문자열 정리 */
private fun String.cleanV(): String =
    trim().replace(ZERO_WIDTH, "").replace(CTRL, "")

/**
 * m.dhlottery.co.kr QR을 표준 URL로 정규화
 * - 이미 /qr.do?method=winQr&v=... 이면 그대로
 * - /?v=... 이면 /qr.do?method=winQr&v=... 로 승격
 * - 그 외는 null
 */
private fun buildCanonicalWinQrUri(from: Uri): Uri? {
    if (!isDhlotteryHost(from.host)) return null

    val lastSeg = from.lastPathSegment
    val isQrPath = lastSeg?.equals("qr.do", ignoreCase = true) == true
    val method = from.getQueryParameter("method")
    val vRaw = from.getQueryParameter("v")
    val v = vRaw?.cleanV()

    return when {
        // 이미 표준 형태
        isQrPath && method.equals("winQr", ignoreCase = true) && !v.isNullOrBlank() -> from

        // 루트 "/?v=..." 같은 형태 → 표준으로 변환
        !isQrPath && !v.isNullOrBlank() -> Uri.Builder()
            .scheme("https")
            .authority("m.dhlottery.co.kr")
            .path("/qr.do") // ← 슬래시 필수
            .appendQueryParameter("method", "winQr")
            .appendQueryParameter("v", v)
            .build()

        else -> null
    }
}

/**
 * v 파라미터 직접 파싱.
 * 포맷: {round}q{A(12)}q{B(12)}q{C(12)}q{D(12)}q{E(12)}{tail...}
 * - 'q' 대/소문자 모두 허용
 * - 각 블록은 2자리×6 = 12자리 (앞 12자리만 사용)
 */
private fun parseDhlotteryV(vRaw: String, sortNumbers: Boolean = true): QrParsed? {
    val s = vRaw.cleanV()
    var i = 0
    val n = s.length

    // 1) 회차: 앞에서부터 연속된 숫자들
    val roundSb = StringBuilder()
    while (i < n && s[i].isDigit()) {
        roundSb.append(s[i]); i++
    }
    val round = roundSb.toString().toIntOrNull() ?: return null

    // 2) 이후 블록들: [문자들] + [숫자들] 반복. 숫자 구간을 블록으로 본다.
    val sets = mutableListOf<List<Int>>()
    while (i < n) {
        // 구분자(숫자 아님) 스킵
        while (i < n && !s[i].isDigit()) i++
        if (i >= n) break

        // 숫자 구간 수집
        val start = i
        while (i < n && s[i].isDigit()) i++
        val block = s.substring(start, i)

        // 앞 12자리만 사용 (6개×2자리)
        if (block.length >= 12) {
            val twelve = block.substring(0, 12)
            val nums = (0 until 6).map { k ->
                twelve.substring(k * 2, k * 2 + 2).toInt()
            }
            if (nums.all { it in 1..45 }) {
                sets += if (sortNumbers) nums.sorted() else nums
            }
        }
    }

    if (sets.isEmpty()) return null
    return QrParsed(round = round, sets = sets)
}


/** URI에서 v를 꺼내 parse */
private fun parseFromQrUri(uri: Uri, sortNumbers: Boolean = true): QrParsed? {
    if (!isDhlotteryHost(uri.host)) return null
    val v = uri.getQueryParameter("v")?.cleanV() ?: return null
    return parseDhlotteryV(v, sortNumbers)
}

// ======================= 메인 함수 ======================
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

    if (openInCustomTab) {
        CustomTabsIntent.Builder().build().launchUrl(context, openUri)
    }

    // canonical 우선 → 실패 시 raw 재시도
    val parsed = parseFromQrUri(openUri, sortNumbers)
        ?: parseFromQrUri(raw, sortNumbers)

    if (parsed != null) {
        onParsed(parsed.round, parsed.sets)
    } else {
        // Log.d(QR_TAG, "parse failed (not a dhlottery QR or invalid v)")
    }
}
