package com.aube.presentation.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aube.presentation.model.LottoUiState
import com.aube.presentation.ui.component.home.LottoResultCard
import com.aube.presentation.ui.component.home.MyNumberCard
import com.aube.presentation.ui.component.home.TodayFortuneCard
import com.aube.presentation.viewmodel.FortuneViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    fortuneViewModel: FortuneViewModel = hiltViewModel(),
    uiState: LottoUiState = remember { LottoUiState.mock() },
    onRoundSelect: (Int) -> Unit = {},
    onRegisterClick: () -> Unit = {},
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        LottoResultCard(
            round = uiState.round,
            date = uiState.date,
            numbers = uiState.winningNumbers,
            bonus = uiState.bonusNumber,
            prize = uiState.totalPrize,
            winners = uiState.winnerCount,
            onRoundSelect = onRoundSelect,
            onSeeStoresClick = {
                val url = "https://m.dhlottery.co.kr/store.do?method=topStore&pageGubun=L645"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        )

        uiState.myNumbers?.let {
            Spacer(Modifier.height(16.dp))
            MyNumberCard(myNumbers = it, result = uiState.matchResult, onRegisterClick = onRegisterClick)
        }

        Spacer(Modifier.height(16.dp))

        val fortuneUiState by fortuneViewModel.state.collectAsState()

        TodayFortuneCard(
            state = fortuneUiState,
        )
    }
}
