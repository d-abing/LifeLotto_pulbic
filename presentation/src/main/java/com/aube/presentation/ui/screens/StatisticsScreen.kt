package com.aube.presentation.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
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

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (ui.isSyncing) "데이터 동기화 중..." else "통계",
                style = MaterialTheme.typography.titleLarge
            )
            if (ui.isSyncing) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        }

        item {
            statisticsSetting(
                rangeFilter = rangeFilter,
                ui = ui,
                vm = statisticsViewModel
            )
        }

        if (ui.stats != null) {
            val stats = ui.stats!!

            item { HighlightRow("🔥 많이 나온 번호 TOP 8", stats.top8) }
            item { HighlightRow("🧊 적게 나온 번호 LOW 8", stats.low8) }
            item { Text("번호별 출현 횟수 (보너스 번호 제외)", style = MaterialTheme.typography.titleMedium) }

            items((1..45).toList(), key = { it }) { n ->
                FrequencyBarRow(
                    number = n,
                    count = stats.freq[n],
                    maxCount = stats.maxCount
                )
            }
        } else {
            // 빈 상태
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxSize(), // Lazy 아이템 뷰포트 꽉 채우기
                    contentAlignment = Alignment.Center
                ) {
                    Text("통계 데이터가 없습니다.")
                }
            }
        }
    }
}

@Composable
private fun statisticsSetting(
    rangeFilter: RangeFilter,
    ui: StatisticsViewModel.UiState,
    vm: StatisticsViewModel,
) {
    Column() {
        Text(text = "통계 설정", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RangeFilter.entries
                .forEach { f ->
                    FilterChip(
                        selected = f == rangeFilter,
                        onClick = { vm.setFilter(f) },
                        label = { Text(f.label) }
                    )
                }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                modifier = Modifier.width(100.dp).height(130.dp),
                selected = ui.useRandomForRecommend,
                onClick = { vm.setUseRandomForRecommend(!ui.useRandomForRecommend) },
                label = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (!ui.useRandomForRecommend) {
                                Icons.Filled.Check
                            } else {
                                Icons.Filled.Close
                            },
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "추천에 통계 사용",
                            textAlign = TextAlign.Center
                        )
                    }
                },
                border = if(!ui.useRandomForRecommend) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else BorderStroke(0.dp, MaterialTheme.colorScheme.primary),
                colors = FilterChipDefaults.filterChipColors().copy(
                    selectedContainerColor = MaterialTheme.colorScheme.surface,
                    selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    labelColor = MaterialTheme.colorScheme.onPrimary,
                    leadingIconColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f) // 오른쪽 Column이 나머지 공간 차지
            ) {
                LabeledSlider(
                    title = "🔥 많이 나온 번호 사용 개수",
                    value = ui.useTopCount,
                    range = 0..6,
                    onChange = vm::setUseTopCount
                )

                LabeledSlider(
                    title = "🧊 적게 나온 번호 사용 개수",
                    value = ui.useLowCount,
                    range = 0..6,
                    onChange = vm::setUseLowCount
                )
            }
        }
    }
}

@Composable
private fun LabeledSlider(
    title: String,
    value: Int,
    range: IntRange,
    onChange: (Int) -> Unit
) {
    Column {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text("${value}개", style = MaterialTheme.typography.bodyMedium)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onChange(it.toInt().coerceIn(range.first, range.last)) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = (range.last - range.first - 1).coerceAtLeast(0)
        )
    }
}

@Composable
private fun HighlightRow(
    title: String,
    pairs: List<Pair<Int, Int>>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 4
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
