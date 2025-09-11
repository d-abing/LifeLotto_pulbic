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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aube.domain.model.LottoSet
import com.aube.presentation.ui.component.home.LottoBall
import kotlinx.coroutines.android.awaitFrame

@Composable
fun ColumnScope.SavedNumbers(
    title: String,
    saved: List<LottoSet>,
    modifier: Modifier = Modifier,
    onDeleteClick: (Int) -> Unit,
) {
    val showSaved = saved.isNotEmpty()

    AnimatedVisibility(
        visible = showSaved,
        modifier = modifier.fillMaxWidth(),
        enter = fadeIn(tween(220)) + expandVertically(
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
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))

                val shownIds = remember { mutableStateMapOf<Int, Boolean>() }
                val listState = rememberLazyListState()
                var prevSize by remember { mutableIntStateOf(saved.size) }

                LaunchedEffect(saved.size) {
                    if (saved.size > prevSize && saved.isNotEmpty()) {
                        awaitFrame()
                        listState.animateScrollToItem(saved.lastIndex)
                    }
                    prevSize = saved.size
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = listState
                ) {
                    items(
                        items = saved,
                        key = { it.id } // id는 안정적으로 유지되어야 함
                    ) { lottoSet ->
                        val alreadyShown = shownIds[lottoSet.id] == true
                        // 처음 나타날 때만 enter 애니메이션 수행, 그 후엔 None
                        val enter = if (alreadyShown) fadeIn(initialAlpha = 0f) else
                            slideInHorizontally(
                                initialOffsetX = { it / 6 },
                                animationSpec = tween(220, easing = FastOutSlowInEasing)
                            ) + fadeIn(tween(200))

                        // 첫 구성 시 한 번만 표시 플래그 남김
                        LaunchedEffect(lottoSet.id) {
                            if (!alreadyShown) shownIds[lottoSet.id] = true
                        }

                        AnimatedVisibility(
                            visible = true,
                            enter = enter,
                            exit = fadeOut(tween(120))
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                lottoSet.numbers.forEach {
                                    LottoBall(it, modifier = Modifier.size(40.dp))
                                }
                                OutlinedButton(
                                    onClick = { onDeleteClick(lottoSet.id) }
                                ) {
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