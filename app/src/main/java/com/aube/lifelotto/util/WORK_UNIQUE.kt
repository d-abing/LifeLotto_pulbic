package com.aube.lifelotto.util

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.aube.domain.util.NotificationScheduler
import com.aube.domain.util.nextDrawInstant
import com.aube.domain.util.roundForDrawInstant
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

const val WORK_UNIQUE = "lotto_result_worker"
private const val KEY_ROUND = "round"
private const val KEY_SCHEDULED_AT = "scheduled_at"

class NotificationSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationScheduler {

    override fun enable() {
        ensureNotificationChannel(context)

        val now = Instant.now()
        val drawInstant = nextDrawInstant(now)
        val target = drawInstant.plusSeconds(180)
        val delayMs = Duration.between(now, target).toMillis().coerceAtLeast(0)
        val round = roundForDrawInstant(drawInstant)

        val data = workDataOf(
            KEY_ROUND to round,
            KEY_SCHEDULED_AT to target.toEpochMilli()
        )

        val req = OneTimeWorkRequestBuilder<LottoResultWorker>()
            .setInputData(data)
            .setInitialDelay(delayMs, java.util.concurrent.TimeUnit.MILLISECONDS)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, java.util.concurrent.TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_UNIQUE, ExistingWorkPolicy.REPLACE, req)
    }

    override fun disable() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_UNIQUE)
    }
}
