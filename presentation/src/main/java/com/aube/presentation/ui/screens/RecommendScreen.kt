package com.aube.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aube.presentation.ui.component.home.LottoBall
import com.aube.presentation.util.generateLottoNumbers

@Composable
fun RecommendScreen(
    recommendedNumbers: List<Int> = generateLottoNumbers(),
    onReroll: () -> Unit = {},
    onSave: (List<Int>) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎉 추천 번호가 도착했어요!", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        Row (horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            recommendedNumbers.forEach { number ->
                LottoBall(number = number)
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = onReroll) {
            Text("다시 추천받기 🔁")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(onClick = { onSave(recommendedNumbers) }) {
            Text("이 번호 저장하기 💾")
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "최근 당첨 통계를 기반으로 추천된 번호입니다.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        Text("📦 저장된 추천 번호", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
    }
}
