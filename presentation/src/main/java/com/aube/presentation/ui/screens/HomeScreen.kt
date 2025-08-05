package com.aube.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aube.presentation.model.LottoUiState
import com.aube.presentation.ui.component.home.LottoResultCard
import com.aube.presentation.ui.component.home.MyNumberCard
import com.aube.presentation.ui.component.home.RoundSelector

@Composable
fun HomeScreen(
    uiState: LottoUiState = remember { LottoUiState.mock() },
    onSeeStoresClick: () -> Unit = {},
    onRoundSelect: (Int) -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    Column(modifier = Modifier.padding(16.dp)) {
        RoundSelector(
            selectedRound = uiState.round,
            onRoundSelect = onRoundSelect
        )

        Spacer(Modifier.height(16.dp))

        LottoResultCard(
            round = uiState.round,
            date = uiState.date,
            numbers = uiState.winningNumbers,
            bonus = uiState.bonusNumber,
            prize = uiState.totalPrize,
            winners = uiState.winnerCount,
            onSeeStoresClick = onSeeStoresClick
        )

        Spacer(Modifier.height(4.dp))

        uiState.myNumbers?.let {
            Spacer(Modifier.height(16.dp))
            MyNumberCard(myNumbers = it, result = uiState.matchResult, onRegisterClick = onRegisterClick)
        }
    }
}
