package com.aube.lifelotto.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aube.domain.model.MyLottoSet
import com.aube.presentation.MainActivity
import com.aube.presentation.R

const val CH_LOTTO = "lotto_results"

fun ensureNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nm = context.getSystemService(NotificationManager::class.java)
        if (nm.getNotificationChannel(CH_LOTTO) == null) {
            nm.createNotificationChannel(
                NotificationChannel(
                    CH_LOTTO,
                    "로또 알림",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }
}

fun showResultNotification(
    context: Context,
    round: Int,
    winning: List<Int>,
    bonus: Int,
    detail: List<Pair<MyLottoSet, Int?>>
) {
    val title = "로또 ${round}회 당첨 결과"
    val summary = "당첨번호 ${winning.joinToString(", ")} + 보너스 $bonus"

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra("round", round) // 선택적으로 회차 정보 전달 가능
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or
                (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
    )

    val style = NotificationCompat.InboxStyle().also { inbox ->
        if (detail.isEmpty()) {
            inbox.addLine("이번 회차에 저장된 내 번호가 없습니다.")
        } else {
            // rank가 있는 항목만 모아서 카운트
            val rankCounts = detail
                .mapNotNull { it.second }        // rank 값만 추출
                .groupingBy { it }
                .eachCount()                     // 등수별 개수

            if (rankCounts.isNotEmpty()) {
                // 등수별로 정렬 후 "1등 1개, 3등 2개" 형식으로 요약
                val summary = rankCounts
                    .toSortedMap()
                    .entries
                    .joinToString(", ") { (rank, count) -> "${rank}등 ${count}개" }

                inbox.addLine("당첨 결과: $summary")
            } else {
                inbox.addLine("아쉽지만 이번 회차는 낙첨되었습니다.")
            }
        }
    }

    val notification = NotificationCompat.Builder(context, CH_LOTTO)
        .setSmallIcon(R.drawable.logo)
        .setContentTitle(title)
        .setContentText(summary)
        .setStyle(style)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    if (Build.VERSION.SDK_INT < 33 ||
        ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        == PackageManager.PERMISSION_GRANTED
    ) {
        NotificationManagerCompat.from(context).notify(2000 + round, notification)
    }
}
