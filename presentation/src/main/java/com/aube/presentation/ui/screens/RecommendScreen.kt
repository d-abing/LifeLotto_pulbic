package com.aube.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aube.presentation.ui.component.home.LottoBall
import com.aube.presentation.ui.component.recommend.SavedNumbers
import com.aube.presentation.viewmodel.RecommendViewModel
import com.aube.presentation.viewmodel.StatisticsViewModel

@Composable
fun RecommendScreen(
    numbers: List<Int>?,
    modifier: Modifier = Modifier,
    onStatisticsClick: () -> Unit,
    recommendViewModel: RecommendViewModel = hiltViewModel(),
    statisticsViewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by recommendViewModel.recommendNumbers.collectAsState()
    val saved by recommendViewModel.savedNumbers.collectAsState()
    val rangeFilter by statisticsViewModel.filterFlow.collectAsState()
    val statisticsUi by statisticsViewModel.ui.collectAsState()

    val statisticsMessage by remember(rangeFilter, statisticsUi.useRandomForRecommend) {
        derivedStateOf {
            if (statisticsUi.useRandomForRecommend) "로 추천된 번호입니다."
            else " 당첨 통계를 기반으로 추천된 번호입니다."
        }
    }

    LaunchedEffect(numbers) {
        if (numbers != null) {
            recommendViewModel.refreshToday(numbers)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎉 추천 번호가 도착했어요!", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 36.dp))

        Row(
            modifier = Modifier.padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.recommended.forEach { LottoBall(it, modifier = Modifier.size(50.dp), style = MaterialTheme.typography.titleMedium) }
        }

        Button(onClick = recommendViewModel::refreshToday) {
            Text(
                text = "다시 추천받기",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                recommendViewModel.saveCurrent()

            }
        ) {
            Text(
                text = "이 번호 저장하기",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    if (!statisticsUi.useRandomForRecommend) {
                        append(rangeFilter.label)
                    } else {
                        append("무작위")
                    }
                }
                append(statisticsMessage)
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "🧐 어떤 통계를 사용할까요? >",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onStatisticsClick() }
        )

        Spacer(Modifier.height(24.dp))

        SavedNumbers(
            saved = saved,
            modifier = Modifier.weight(1f),
            recommendViewModel = recommendViewModel
        )
    }
}

