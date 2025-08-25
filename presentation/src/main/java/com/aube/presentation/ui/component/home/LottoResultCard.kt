package com.aube.presentation.ui.component.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LottoResultCard(
    round: Int,
    date: String,
    numbers: List<Int>,
    bonus: Int,
    prize: String,
    winners: Int,
    onRoundSelect: (Int) -> Unit = {},
    onSeeStoresClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            RoundSelector(round, onRoundSelect)

            Spacer(Modifier.height(8.dp))

            Text(text = date, style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                numbers.forEach {
                    LottoBall(number = it)
                    Spacer(Modifier.width(4.dp))
                }
                Text("+")
                Spacer(Modifier.width(4.dp))
                LottoBall(number = bonus)
            }

            Spacer(Modifier.height(8.dp))

            Text("1등 총 당첨금: $prize (${winners}명)")

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onSeeStoresClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("당첨 판매점 보기")
            }
        }
    }
}
