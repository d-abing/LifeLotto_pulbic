package com.aube.lifelotto.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aube.domain.repository.MyLottoRepository
import com.aube.domain.service.NotificationScheduler
import com.aube.domain.usecase.GetLottoResultUseCase
import com.aube.domain.util.nextDrawInstant
import com.aube.domain.util.rankOf
import com.aube.domain.util.roundForDrawInstant
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class LottoResultWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val getLottoResultUseCase: GetLottoResultUseCase,
    private val myRepo: MyLottoRepository,
    private val scheduler: NotificationScheduler, // 새로 주입!
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        ensureNotificationChannel(applicationContext)

        val targetInstant = nextDrawInstant() // domain 함수
        val round = roundForDrawInstant(targetInstant)

        // 1) 결과 가져오기
        val result = runCatching { getLottoResultUseCase(round) }.getOrElse {
            return Result.retry()
        }

        val winning = result.winningNumbers
        val bonus = result.bonus
        if (winning.isNullOrEmpty() || winning.size < 6) {
            return Result.retry()
        }

        // 2) 내 번호 조회 (회차별로 바로 가져오는 메서드 추천)
        val myThisRound = myRepo.getBeforeDraw(round)

        // 3) 등수 계산
        val items = myThisRound.map { set ->
            set to rankOf(set.numbers, winning, bonus) // domain 함수
        }

        // 4) 알림 표시
        showResultNotification(
            context = applicationContext,
            round = round,
            winning = winning,
            bonus = bonus,
            detail = items
        )

        // 5) 다음 회차 예약
        scheduler.enable()

        return Result.success()
    }
}
