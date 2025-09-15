package com.aube.lifelotto.util
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.aube.domain.model.MyLottoSet
import com.aube.domain.repository.MyLottoNumbersRepository
import com.aube.domain.usecase.GetLottoResultUseCase
import com.aube.domain.util.nextDrawInstant
import com.aube.domain.util.rankOf
import com.aube.domain.util.roundForDrawInstant
import com.aube.presentation.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

const val CH_Lotto = "lotto_results"

fun ensureNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= 26) {
        val nm = context.getSystemService(NotificationManager::class.java)
        if (nm.getNotificationChannel(CH_Lotto) == null) {
            nm.createNotificationChannel(
                NotificationChannel(CH_Lotto, "로또 알림", NotificationManager.IMPORTANCE_DEFAULT)
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
                    "$tag) ${set.numbers.joinToString(" ")}  →  ${rank}등"
                } else {
                    "$tag) ${set.numbers.joinToString(" ")}  →  낙첨"
                }
                inbox.addLine(line)
            }
        }
    }

    val n = NotificationCompat.Builder(context, CH_Lotto)
        .setSmallIcon(R.drawable.logo)
        .setContentTitle(title)
        .setContentText(summary)
        .setStyle(style)
        .setAutoCancel(true)
        .build()

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    NotificationManagerCompat.from(context).notify(1001, n)
}

fun scheduleNextResultCheck(context: Context) {
    val now = Instant.now()
    val target = nextDrawInstant(now).plusSeconds(180)
    val delayMs = Duration.between(now, target).toMillis().coerceAtLeast(0)

    val req = OneTimeWorkRequestBuilder<LottoResultWorker>()
        .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()

    WorkManager.getInstance(context)
        .enqueueUniqueWork(WORK_UNIQUE, ExistingWorkPolicy.REPLACE, req)
}

@HiltWorker
class LottoResultWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val getLottoResultUseCase: GetLottoResultUseCase,
    private val myRepo: MyLottoNumbersRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        ensureNotificationChannel(applicationContext)

        val targetInstant = nextDrawInstant() // 지금 기준 다음 토 20:45 (스케줄 시점과 약간 차 있어도 OK)
        val round = roundForDrawInstant(targetInstant)

        // 1) 결과 가져오기
        val result = runCatching { getLottoResultUseCase(round) }.getOrElse {
            // 네트워크나 서버 대기: 재시도
            return Result.retry()
        }

        // dhlottery JSON이 아직 준비 전이면 보통 실패/빈값 신호를 줄 수 있음
        val winning = result.winningNumbers
        val bonus = result.bonus
        if (winning.isNullOrEmpty() || winning.size < 6) {
            return Result.retry()
        }

        // 2) 이번 회차 내 번호 가져오기 (round 기준)
        val myAll = myRepo.getMyNumbersHistory()           // [MyLottoSet]
        val myThisRound = myAll.filter { it.round == round }

        // 3) 등수 계산
        val items = myThisRound.map { set ->
            val r = rankOf(set.numbers, winning, bonus)
            set to r
        }

        // 4) 알림 만들기
        showResultNotification(
            context = applicationContext,
            round = round,
            winning = winning,
            bonus = bonus,
            detail = items
        )

        // 5) 다음 회차 재스케줄
        scheduleNextResultCheck(applicationContext)

        return Result.success()
    }
}
