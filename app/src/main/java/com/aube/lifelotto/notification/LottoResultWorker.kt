package com.aube.lifelotto.notification

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aube.domain.repository.MyLottoRepository
import com.aube.domain.service.NotificationScheduler
import com.aube.domain.usecase.GetLottoResultUseCase
import com.aube.domain.util.rankOf
import com.aube.domain.util.roundForDrawInstant
import com.aube.lifelotto.notification.LottoResultWorkContract.KEY_ROUND
import com.aube.lifelotto.notification.LottoResultWorkContract.KEY_SCHEDULED_AT
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant

@HiltWorker
class LottoResultWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val getLottoResultUseCase: GetLottoResultUseCase,
    private val myRepo: MyLottoRepository,
    private val scheduler: NotificationScheduler,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        ensureNotificationChannel(applicationContext)

        val scheduledAtMillis = inputData.getLong(KEY_SCHEDULED_AT, 0L)
        if (scheduledAtMillis <= 0L) {
            // Log.w("LottoResultWorker", "Retry: missing scheduledAt")
            return Result.retry()
        }
        val scheduledInstant = Instant.ofEpochMilli(scheduledAtMillis)

        val inputRound = inputData.getInt(KEY_ROUND, -1)
        var round = if (inputRound > 0) inputRound else roundForDrawInstant(scheduledInstant)
        // Log.d("LottoResultWorker", "input round=$inputRound -> using round=$round")

        val expireWindow = java.time.Duration.ofHours(2)   // 2시간
        if (Instant.now().isAfter(scheduledInstant.plus(expireWindow))) {
            // Log.w("LottoResultWorker", "Expired window: skip this round (round=$round). Scheduling next.")
            scheduler.enable()          // 다음 회차 예약
            return Result.success()     // 이번 작업은 조용히 종료
        }

        val result = runCatching { getLottoResultUseCase(round) }.getOrElse {
            // Log.e("LottoResultWorker", "Retry: getLottoResult failed for round=$round", it)
            return Result.retry()
        }

        val winning = result.winningNumbers
        val bonus = result.bonus
        if (winning.isNullOrEmpty() || winning.size < 6) {
            // Log.w("LottoResultWorker", "Retry: result not ready (round=$round)")
            return Result.retry()
        }

        val myThisRound = myRepo.getBeforeDraw(round)
        val items = myThisRound.map { set -> set to rankOf(set.numbers, winning, bonus) }

        showResultNotification(
            context = applicationContext,
            round = round,
            winning = winning,
            bonus = bonus,
            detail = items
        )

        scheduler.enable()
        return Result.success()
    }
}