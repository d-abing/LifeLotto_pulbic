package com.aube.presentation.ui.component.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aube.presentation.R
import com.aube.presentation.model.MatchResult
import com.aube.presentation.model.MyLottoNumbersUiState

@Composable
fun MyNumberCard(
    uiState: MyLottoNumbersUiState,
    isBlurred: Boolean,
    onVisibilityClick: () -> Unit,
    onQRCodeClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val beforeDraw = uiState.beforeDraw
    val myNumbers = uiState.myNumbers
    val matchResult = uiState.matchResult
    val matchHistory = uiState.matchHistory

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("내 번호 결과", style = MaterialTheme.typography.titleLarge)

                    Icon(
                        painter = if(isBlurred) painterResource(R.drawable.visibility)
                        else painterResource(R.drawable.visibility_off),
                        contentDescription = "Visibility",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onVisibilityClick()
                        }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.qr_code),
                        contentDescription = "QR Code",
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onQRCodeClick()
                        }
                    )

                    if (!beforeDraw.isNullOrEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onRegisterClick()
                            }
                        )
                    }
                }
            }

            val blurRadius = if (isBlurred) 12.dp else 0.dp

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardDefaults.shape)
                    .blur(blurRadius),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "추첨 전",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (beforeDraw.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = onRegisterClick) {
                            Text("내 로또 번호 등록하기")
                        }
                    }
                } else {
                    beforeDraw.forEach {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            it.numbers.forEach {
                                LottoBall(number = it)
                                Spacer(Modifier.width(4.dp))
                            }
                        }
                    }
                }

                RealtimeDdayText()

                if (myNumbers.isNotEmpty()) {
                    Text(
                        text = "이번 회차 당첨 결과",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    myNumbers.forEach {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            it.numbers.forEach {
                                LottoBall(number = it)
                                Spacer(Modifier.width(4.dp))
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (matchResult) {
                            is MatchResult.Win -> Text(
                                "\uD83C\uDF89 ${matchResult.rank}등 당첨! 축하합니다 \uD83C\uDF89",
                                fontWeight = FontWeight.SemiBold
                            )

                            is MatchResult.Lose -> Text("아쉽게도 낙첨되었습니다.")
                            else -> {}
                        }
                    }
                }

                Text(
                    text = "지난 회차 당첨 결과",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                LottoStatsTable(matchHistory)
            }
        }
    }
}


@Composable
fun LottoStatsTable(
    matchHistory: List<Int>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("1등", "2등", "3등", "4등", "5등").forEach { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val history = intArrayOf(
                matchHistory.count { it == 1 },
                matchHistory.count { it == 2 },
                matchHistory.count { it == 3 },
                matchHistory.count { it == 4 },
                matchHistory.count { it == 5 }
            )

            history.forEach { value ->
                Text(
                    text = "${value}회",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}