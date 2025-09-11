package com.aube.presentation.ui.component.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LottoResultCard(
    isLoading: Boolean,
    latestRound: Int,
    round: Int,
    date: String,
    numbers: List<Int>,
    bonus: Int,
    prize: String,
    winners: Int,
    onRoundSelect: (Int) -> Unit = {},
    onSeeStoresClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 220.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RoundSelector(latestRound, round, date, onRoundSelect)

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        numbers.forEach {
                            LottoBall(number = it)
                            Spacer(Modifier.width(4.dp))
                        }
                        Text("+")
                        Spacer(Modifier.width(4.dp))
                        LottoBall(number = bonus)
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("1등 당첨금: $prize (${winners}명)")

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
    }
}