package com.aube.presentation.ui.component

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aube.domain.model.LottoSet
import com.aube.presentation.R
import com.aube.presentation.ui.component.home.LottoBall
import kotlinx.coroutines.android.awaitFrame

@Composable
fun ColumnScope.SavedNumbers(
    title: String,
    saved: List<LottoSet>,
    modifier: Modifier = Modifier,
    onDeleteClick: (Int) -> Unit,
    onExpandToggle: (() -> Unit),
    onDeleteAll: (() -> Unit),
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
            // ▸ 확대/전체삭제 상태
            var expanded by rememberSaveable { mutableStateOf(true) }
            var showConfirmDeleteAll by rememberSaveable { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onExpandToggle()
                    }
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // ───────────────── Title + Actions ─────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    Icon(
                        painter = painterResource(R.drawable.delete_sweep),
                        modifier = Modifier.size(24.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                showConfirmDeleteAll = true
                            },
                        contentDescription = "전체 삭제"
                    )
                }

                Spacer(Modifier.height(16.dp))

                val shownIds = remember { mutableStateMapOf<Int, Boolean>() }
                val listState = rememberLazyListState()
                var prevIds by remember { mutableStateOf<Set<Int>>(emptySet()) }

                val groupedByRound = remember(saved) { saved.groupBy { it.round } }

                // 회차 오름차순, null(회차 없음)은 맨 뒤
                val orderedRounds = remember(groupedByRound) {
                    buildList {
                        addAll(groupedByRound.keys.filterNotNull().sorted())
                        if (groupedByRound.containsKey(null)) add(null)
                    }
                }

                val flat: List<RowModel> = remember(orderedRounds, groupedByRound) {
                    orderedRounds.flatMap { round ->
                        val sets = groupedByRound[round].orEmpty()
                        val header = round?.let { listOf(RowModel.Header(it)) } ?: emptyList()
                        header + sets.map { RowModel.Item(it) }
                    }
                }

                LaunchedEffect(saved.map { it.id }) {
                    val currIds = saved.map { it.id }.toSet()
                    val added = currIds - prevIds
                    prevIds = currIds

                    if (added.isNotEmpty()) {
                        val targetId = added.last()
                        val flatIndex = flat.indexOfFirst { it is RowModel.Item && it.set.id == targetId }
                        if (flatIndex >= 0) {
                            awaitFrame() // 레이아웃 안정화
                            listState.animateScrollToItem(flatIndex)
                        }
                    }
                }

                // ▸ expanded 여부에 따라 높이 조절
                val listModifier = if (expanded) {
                    Modifier.weight(1f)
                } else {
                    Modifier.heightIn(max = 200.dp) // 접힘 상태 최대 높이
                }

                LazyColumn(
                    modifier = listModifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = listState
                ) {
                    items(
                        items = flat,
                        key = {
                            when (it) {
                                is RowModel.Header -> "header-${it.round}"
                                is RowModel.Item   -> it.set.id
                            }
                        }
                    ) { row ->
                        when (row) {
                            is RowModel.Header -> {
                                Text(
                                    text = "${row.round}회",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            is RowModel.Item -> {
                                val lottoSet = row.set
                                val alreadyShown = shownIds[lottoSet.id] == true
                                val enter = if (alreadyShown) fadeIn(initialAlpha = 0f) else
                                    slideInHorizontally(
                                        initialOffsetX = { it / 6 },
                                        animationSpec = tween(220, easing = FastOutSlowInEasing)
                                    ) + fadeIn(tween(200))

                                LaunchedEffect(lottoSet.id) {
                                    if (!alreadyShown) shownIds[lottoSet.id] = true
                                }

                                AnimatedVisibility(visible = true, enter = enter, exit = fadeOut(tween(120))) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            lottoSet.numbers.forEach {
                                                LottoBall(it, modifier = Modifier.size(40.dp))
                                            }
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        OutlinedButton(onClick = { onDeleteClick(lottoSet.id) }) {
                                            Text("삭제", style = MaterialTheme.typography.labelMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ─────────────── 전체 삭제 확인 다이얼로그 ───────────────
            if (showConfirmDeleteAll) {
                AlertDialog(
                    containerColor = MaterialTheme.colorScheme.background,
                    onDismissRequest = { showConfirmDeleteAll = false },
                    title = { Text("전체 삭제") },
                    text = { Text("저장된 번호를 모두 삭제할까요? 이 작업은 되돌릴 수 없습니다.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showConfirmDeleteAll = false
                            onDeleteAll?.invoke()
                        }) { Text(
                            text = "삭제",
                            color = MaterialTheme.colorScheme.error
                        ) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDeleteAll = false }) { Text("취소") }
                    }
                )
            }
        }
    }
}

private sealed interface RowModel {
    data class Header(val round: Int) : RowModel
    data class Item(val set: LottoSet) : RowModel
}