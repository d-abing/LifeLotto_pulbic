package com.aube.presentation.ui.component.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aube.presentation.viewmodel.FortuneUiState

@Composable
fun TodayFortuneCard(
    state: FortuneUiState,
    onRecommendWithLuckyNumbers: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("오늘의 재물운", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(4.dp))

            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else state.fortune?.let { f ->
                Text("${f.score} 점", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, modifier =  Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text(f.summary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(Modifier.height(4.dp))
                Text("럭키 넘버: ${f.luckyNumbers.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                Text("좋은 시간대: ${f.luckyTime}", style = MaterialTheme.typography.bodyMedium)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = onRecommendWithLuckyNumbers) {
                    Text("럭키 넘버 포함 번호 추천 받기")
                }
            }
        }
    }
}