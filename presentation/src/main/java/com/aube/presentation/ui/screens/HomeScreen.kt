package com.aube.presentation.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aube.presentation.ui.component.home.LottoResultCard
import com.aube.presentation.ui.component.home.MyNumberCard
import com.aube.presentation.ui.component.home.TodayFortuneCard
import com.aube.presentation.viewmodel.FortuneViewModel
import com.aube.presentation.viewmodel.LottoViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    fortuneViewModel: FortuneViewModel = hiltViewModel(),
    lottoViewModel: LottoViewModel,
    onRegisterClick: () -> Unit,
    onRecommendWithLuckyNumbers: () -> Unit
) {
    val context = LocalContext.current
    val latestRound = remember { estimateLatestRound() }
    val lottoUiState by lottoViewModel.uiState.collectAsState()
    val fortuneUiState by fortuneViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        lottoViewModel.loadLotto(latestRound)
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "인생 대박 로또 복권",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "오늘의 번호가 내일의 인생을 바꾼다.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        LottoResultCard(
            latestRound = latestRound,
            round = lottoUiState.round,
            date = lottoUiState.date,
            numbers = lottoUiState.winningNumbers,
            bonus = lottoUiState.bonus,
            prize = lottoUiState.firstPrize,
            winners = lottoUiState.firstCount,
            onRoundSelect = { round -> lottoViewModel.loadLotto(round) },
            onSeeStoresClick = {
                val url = "https://m.dhlottery.co.kr/store.do?method=topStore&pageGubun=L645"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        )

        Spacer(Modifier.height(12.dp))

        MyNumberCard(
            myNumbers = lottoUiState.myNumbers,
            result = lottoUiState.matchResult,
            onRegisterClick = onRegisterClick,
        )

        Spacer(Modifier.height(12.dp))

        TodayFortuneCard(
            state = fortuneUiState,
            onRecommendWithLuckyNumbers = onRecommendWithLuckyNumbers
        )
    }
}

private fun estimateLatestRound(): Int {
    val firstDrawDate = LocalDate.of(2002, 12, 7)
    val today = LocalDate.now()
    val weeks = ChronoUnit.WEEKS.between(firstDrawDate, today)
    return weeks.toInt() + 1
}

