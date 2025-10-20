package com.aube.presentation.ui.screens

import RoundSelectorSimple
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aube.domain.model.LottoSet
import com.aube.presentation.ui.component.SavedNumbers
import com.aube.presentation.ui.component.home.LottoBall
import com.aube.presentation.viewmodel.LottoViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    lottoViewModel: LottoViewModel = hiltViewModel(),
) {
    val myLottoNumbersUiState by lottoViewModel.myLottoNumbersUiState.collectAsState()
    val newCombination by lottoViewModel.newCombination.collectAsState()
    var selectedRound by remember { mutableStateOf<Int?>(null) }
    var isExpanded by remember { mutableStateOf(false) }
    var showConfirmDeleteAll by rememberSaveable { mutableStateOf(false) }
    val beforeDraw = myLottoNumbersUiState.beforeDraw

    LaunchedEffect(beforeDraw.size) {
        if (beforeDraw.isEmpty()) {
            isExpanded = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!isExpanded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("수동으로 번호 추가하기", style = MaterialTheme.typography.titleLarge)

                Button(
                    onClick = {
                        if (newCombination.size == 6) {
                            lottoViewModel.saveMyLottoNumbers(newCombination, selectedRound)
                        }
                    }
                ) {
                    Text(text = "저장하기")
                }
            }

            RoundSelectorSimple(
                modifier = Modifier.fillMaxWidth()
            ) { r -> selectedRound = r }

            NewCombination(newCombination) {
                lottoViewModel.deleteNumberFromCombination(it)
            }
            NumberPad {
                lottoViewModel.addNumberToCombination(it)
            }
        }

        SavedNumbers(
            title = "추첨 전 내 로또 번호",
            saved = myLottoNumbersUiState.beforeDraw.map { 
                LottoSet(
                    id = it.id,
                    round = it.round,
                    numbers = it.numbers,
                    date = it.date
                )
            },
            modifier = Modifier.weight(1f),
            onDeleteClick = { id ->
                lottoViewModel.deleteMyLottoNumbers(id)
            },
            onExpandToggle = {
                isExpanded = !isExpanded
            },
            onDeleteAll = {
                lottoViewModel.deleteBeforeDraw()
            }
        )

        Text(
            text = "내 로또 번호 기록 전체 삭제",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    showConfirmDeleteAll = true
                },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
        )

        // ─────────────── 전체 삭제 확인 다이얼로그 ───────────────
        if (showConfirmDeleteAll) {
            AlertDialog(
                containerColor = MaterialTheme.colorScheme.background,
                onDismissRequest = { showConfirmDeleteAll = false },
                title = { Text("전체 삭제") },
                text = { Text("저장된 내 로또 번호를 모두 삭제할까요? 이 작업은 되돌릴 수 없습니다.") },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirmDeleteAll = false
                        lottoViewModel.deleteAll()
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

@Composable
fun NewCombination(
    newCombination: List<Int>,
    removeColor: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(1.dp, color = Color.LightGray, shape = RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center // 전체 박스 안에서 가운데
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            newCombination.forEach { number ->
                LottoBall(
                    number = number,
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { removeColor(number) }
                )
            }
        }
    }
}

@Composable
fun NumberPad(addNumber: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(9),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color = Color.LightGray, shape = RoundedCornerShape(16.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(45) { i ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { addNumber(i + 1) },
                contentAlignment = Alignment.Center
            ) {
                LottoBall(number = i + 1, modifier = Modifier.fillMaxSize(0.9f))
            }
        }
    }
}