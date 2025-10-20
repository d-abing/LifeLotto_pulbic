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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aube.presentation.ui.component.home.LottoResultCard
import com.aube.presentation.ui.component.home.MyNumberCard
import com.aube.presentation.ui.component.home.TodayFortuneCard
import com.aube.presentation.viewmodel.FortuneViewModel
import com.aube.presentation.viewmodel.LottoViewModel
import handleDhlotteryQr

@OptIn(ExperimentalMaterial3Api::class)
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
    var showScanner by remember { mutableStateOf(false) }

    // ✅ 처음 진입 시 로드
    LaunchedEffect(Unit) { lottoViewModel.loadHome() }
    LaunchedEffect(latestRound) { lottoViewModel.loadHome() }

    // ✅ Pull-to-Refresh 상태
    var isRefreshing by remember { mutableStateOf(false) }
    val pullState = rememberPullToRefreshState()

    // 로딩 끝나면 스피너 닫기
    LaunchedEffect(lottoUiState.isLoading) {
        if (!lottoUiState.isLoading) isRefreshing = false
    }

    PullToRefreshBox(
        modifier = modifier,                    // 바깥 modifier 그대로 사용
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            lottoViewModel.loadHome()
        },
        state = pullState,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                state = pullState,
                containerColor = MaterialTheme.colorScheme.tertiary, // 원 배경색 🎨
                color = MaterialTheme.colorScheme.onTertiary, // 안쪽 아이콘 색상
            )
        }
    ) {
        // ⬇️ 기존 Column 내용 그대로
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "인생 대박 로또 복권",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "오늘의 번호가 내일의 인생을 바꾼다.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            LottoResultCard(
                isLoading = lottoUiState.isLoading,
                latestRound = latestRound,
                round = lottoUiState.round,
                date = lottoUiState.date,
                numbers = lottoUiState.winningNumbers,
                bonus = lottoUiState.bonus,
                prize = lottoUiState.firstPrize,
                winners = lottoUiState.firstCount,
                onRoundSelect = { round -> lottoViewModel.refreshLotto(round) },
                onSeeStoresClick = {
                    val url = "https://m.dhlottery.co.kr/store.do?method=topStore&pageGubun=L645"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            )

            Spacer(Modifier.height(12.dp))

            val blurred by lottoViewModel.isBlurred.collectAsStateWithLifecycle()

            MyNumberCard(
                uiState = myLottoNumbersUiState,
                isBlurred = blurred,
                onVisibilityClick = { lottoViewModel.toggleBlur() },
                onQRCodeClick = { showScanner = true },
                onRegisterClick = onRegisterClick,
            )

            Spacer(Modifier.height(12.dp))

            TodayFortuneCard(
                state = fortuneUiState,
                onRecommendWithLuckyNumbers = onRecommendWithLuckyNumbers
            )
        }
    }

    // QR 스캐너 그대로
    if (showScanner) {
        QrScreen(
            onQrDetected = { url ->
                handleDhlotteryQr(
                    context = context,
                    url = url,
                    onParsed = { round, numbers ->
                        lottoViewModel.onQrParsed(round, numbers)
                    }
                )
                showScanner = false
            },
            onClose = { showScanner = false }
        )
    }
}