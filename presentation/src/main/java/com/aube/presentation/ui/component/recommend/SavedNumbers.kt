package com.aube.presentation.ui.component.recommend

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aube.domain.model.LottoSet
import com.aube.presentation.ui.component.home.LottoBall
import com.aube.presentation.viewmodel.RecommendViewModel
import kotlinx.coroutines.delay


@Composable
fun ColumnScope.SavedNumbers(
    saved: List<LottoSet>,
    modifier: Modifier,
    recommendViewModel: RecommendViewModel,
) {
    val showSaved = saved.isNotEmpty()

    AnimatedVisibility(
        visible = showSaved,
        enter = fadeIn(animationSpec = tween(220)) +
                expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ),
        exit = shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(180)
        ) + fadeOut(tween(180))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            ),
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("📦 저장된 추천 번호", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = saved,
                        key = { it.id }
                    ) { lottoSet ->
                        var itemVisible by remember { mutableStateOf(false) }
                        LaunchedEffect(lottoSet.id) {
                            delay((saved.indexOf(lottoSet) * 40L).coerceAtMost(240L))
                            itemVisible = true
                        }

                        AnimatedVisibility(
                            visible = itemVisible,
                            enter = slideInHorizontally(
                                initialOffsetX = { it / 6 },
                                animationSpec = tween(220, easing = FastOutSlowInEasing)
                            ) + fadeIn(tween(200)),
                            exit = fadeOut(tween(120))
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                lottoSet.numbers.forEach {
                                    LottoBall(it, modifier = Modifier.size(40.dp))
                                }
                                OutlinedButton(onClick = { recommendViewModel.deleteCurrent(lottoSet.id) }) {
                                    Text("삭제", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
