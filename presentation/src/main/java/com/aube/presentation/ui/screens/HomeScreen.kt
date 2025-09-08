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

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    fortuneViewModel: FortuneViewModel = hiltViewModel(),
    lottoViewModel: LottoViewModel,
    onRegisterClick: () -> Unit,
    onRecommendWithLuckyNumbers: (List<Int>) -> Unit
) {
    val context = LocalContext.current
    val latestRound by lottoViewModel.latestRound.collectAsState()
    val lottoUiState by lottoViewModel.lottoUiState.collectAsState()
    val myLottoNumbersUiState by lottoViewModel.myLottoNumbersUiState.collectAsState()
    val fortuneUiState by fortuneViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        lottoViewModel.loadHome()
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
            myNumbers = myLottoNumbersUiState.myNumbers,
            result = myLottoNumbersUiState.matchResult,
            onRegisterClick = onRegisterClick,
        )

        Spacer(Modifier.height(12.dp))

        TodayFortuneCard(
            state = fortuneUiState,
            onRecommendWithLuckyNumbers = { luckyNumbers -> onRecommendWithLuckyNumbers(luckyNumbers) }
        )
    }
}
