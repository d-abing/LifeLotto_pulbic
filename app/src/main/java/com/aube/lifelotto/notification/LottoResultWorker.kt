package com.aube.lifelotto.notification

import android.content.Context
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
        if (scheduledAtMillis <= 0L) return Result.retry()
        val scheduledInstant = Instant.ofEpochMilli(scheduledAtMillis)

        val inputRound = inputData.getInt(KEY_ROUND, -1)
        var round = if (inputRound > 0) inputRound else roundForDrawInstant(scheduledInstant)

        // 만료 윈도우(발표 시점+2h 지나면 이번 작업은 종료하고 다음 회차 예약)
        val expireWindow = java.time.Duration.ofHours(2)
        if (Instant.now().isAfter(scheduledInstant.plus(expireWindow))) {
            scheduler.enable()   // 다음 회차 예약
            return Result.success()
        }

        // 결과 가져오기 (nullable)
        val result = runCatching { getLottoResultUseCase(round) }.getOrNull()
        if (result == null) {
            // 발표 전/미발표 또는 일시 실패 → 재시도
            return Result.retry()
        }

        val winning = result.winningNumbers
        val bonus = result.bonus
        // 안전 가드 (번호 6개 + 보너스 필수)
        if (winning == null || winning.size != 6 || bonus == null) {
            return Result.retry()
        }

        // 등수 계산
        val myThisRound = myRepo.getBeforeDraw(round)
        val items = myThisRound.map { set -> set to rankOf(set.numbers, winning, bonus) }

        // 알림 발송
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