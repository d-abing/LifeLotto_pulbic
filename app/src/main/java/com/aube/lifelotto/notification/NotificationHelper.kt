package com.aube.lifelotto.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aube.domain.model.MyLottoSet

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

    val style = NotificationCompat.InboxStyle().also { inbox ->
        if (detail.isEmpty()) {
            inbox.addLine("이번 회차에 저장된 내 번호가 없습니다.")
        } else {
            detail.forEachIndexed { idx, (set, rank) ->
                val tag = ('A' + idx).toString()
                val line = if (rank != null) {
                    "$tag) ${set.numbers.joinToString(" ")} → ${rank}등"
                } else {
                    "$tag) ${set.numbers.joinToString(" ")} → 낙첨"
                }
                inbox.addLine(line)
            }
        }
    }

    val notification = NotificationCompat.Builder(context, CH_LOTTO)
        .setSmallIcon(com.aube.presentation.R.drawable.logo)
        .setContentTitle(title)
        .setContentText(summary)
        .setStyle(style)
        .setAutoCancel(true)
        .build()

    if (Build.VERSION.SDK_INT < 33 || ActivityCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        NotificationManagerCompat.from(context).notify(2000 + round, notification)
    }
}