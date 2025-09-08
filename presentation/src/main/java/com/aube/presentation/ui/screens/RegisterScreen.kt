package com.aube.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aube.presentation.ui.component.home.LottoBall
import com.aube.presentation.viewmodel.LottoViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    lottoViewModel: LottoViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val myLottoNumbersUiState by lottoViewModel.myLottoNumbersUiState.collectAsState()
    val newCombination by lottoViewModel.newCombination.collectAsState()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            {
                if (newCombination.size == 6) {
                    lottoViewModel.saveMyLottoNumbers(newCombination)
                }
            }
        ) {
            Text(text = "저장하기")
        }

        NewCombination(newCombination) {
            lottoViewModel.deleteNumberFromCombination(it)
        }
        NumberPad {
            lottoViewModel.addNumberToCombination(it)
        }


        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "내 로또 번호", style = MaterialTheme.typography.titleMedium)

                myLottoNumbersUiState.myNumbers?.forEach {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        it.forEach {
                            LottoBall(number = it)
                            Spacer(Modifier.width(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewCombination(newCombination: List<Int>, removeColor: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(1.dp, color = Color.LightGray, shape = RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
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

@Composable
fun NumberPad(addNumber: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(9),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, color = Color.LightGray, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(45) { i ->
            LottoBall(
                number = i + 1,
                modifier = Modifier
                    .size(30.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { addNumber(i + 1) }
            )
        }
    }
}