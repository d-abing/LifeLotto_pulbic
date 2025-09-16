package com.aube.lifelotto.notification

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.aube.domain.service.NotificationScheduler
import com.aube.domain.util.nextDrawInstant
import com.aube.domain.util.roundForDrawInstant
import com.aube.lifelotto.notification.LottoResultWorkContract.KEY_ROUND
import com.aube.lifelotto.notification.LottoResultWorkContract.KEY_SCHEDULED_AT
import com.aube.lifelotto.notification.LottoResultWorkContract.WORK_UNIQUE
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject



class NotificationSchedulerImpl @Inject constructor(
    private val workManager: WorkManager,
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
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork(WORK_UNIQUE, ExistingWorkPolicy.REPLACE, req)
    }

    override fun disable() {
        workManager.cancelUniqueWork(WORK_UNIQUE)
    }
}