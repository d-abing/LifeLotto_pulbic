package com.aube.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aube.presentation.ui.component.home.LottoBall
import com.aube.presentation.viewmodel.RecommendViewModel

@Composable
fun RecommendScreen(
    modifier: Modifier = Modifier,
    vm: RecommendViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎉 추천 번호가 도착했어요!", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(40.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state.recommended.forEach { LottoBall(it, modifier = Modifier.size(50.dp), style = MaterialTheme.typography.titleMedium) }
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = vm::refreshToday) {
            Text(
                text = "다시 추천받기 🔁",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(onClick = vm::saveCurrent) {
            Text(
                text = "이 번호 저장하기 💾",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "최근 당첨 통계를 기반으로 추천된 번호입니다.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        Text("📦 저장된 추천 번호 보기", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            /*items(saved) { lottoSet ->
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    lottoSet.numbers.forEach {
                        LottoBall(it, modifier = Modifier.size(20.dp))
                    }
                }
            }*/
        }
    }
}
