package com.aube.presentation.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aube.presentation.model.RangeFilter
import com.aube.presentation.ui.component.home.LottoBall
import com.aube.presentation.viewmodel.StatisticsViewModel

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    statisticsViewModel: StatisticsViewModel = hiltViewModel()
) {
    val ui by statisticsViewModel.ui.collectAsState()
    val rangeFilter by statisticsViewModel.filterFlow.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = if (ui.isSyncing) "데이터 동기화 중..." else "통계 회차 범위",
                style = MaterialTheme.typography.titleLarge
            )

            if (ui.isSyncing) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RangeFilter.values().forEach { f ->
                        FilterChip(
                            selected = f == rangeFilter,
                            onClick = { statisticsViewModel.setFilter(f) },
                            label = { Text(f.label) }
                        )
                    }
                }
            }
        }

        ui.stats?.let { stats ->
            HighlightRow("🔥 많이 나온 번호 TOP 6", stats.top6)
            HighlightRow("🧊 적게 나온 번호 LOW 6", stats.low6)

            Text("번호별 출현 횟수 (보너스 번호 제외)", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items((1..45).toList(), key = { it }) { n ->
                    FrequencyBarRow(
                        number = n,
                        count = stats.freq[n],
                        maxCount = stats.maxCount
                    )
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("통계 데이터가 없습니다.")
        }
    }
}

@Composable
private fun HighlightRow(
    title: String,
    pairs: List<Pair<Int, Int>>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        // FlowRow: foundation에 포함(Compose 1.6+) / 없으면 Accompanist FlowLayout
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 3
        ) {
            pairs.forEach { (num, cnt) ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 2.dp,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LottoBall(num, modifier = Modifier.size(28.dp))
                        Text("x$cnt", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun FrequencyBarRow(
    number: Int,
    count: Int,
    maxCount: Int
) {
    val fraction by animateFloatAsState(
        targetValue = if (maxCount == 0) 0f else count.toFloat() / maxCount,
        animationSpec = tween(300),
        label = "bar"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LottoBall(number, modifier = Modifier.size(32.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(18.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(9.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
            )
        }

        Text(count.toString(), style = MaterialTheme.typography.labelLarge)
    }
}
