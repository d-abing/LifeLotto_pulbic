package com.aube.presentation.ui.component.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aube.presentation.model.MatchResult

@Composable
fun MyNumberCard(
    myNumbers: List<List<Int>>?,
    result: MatchResult?,
    onRegisterClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("내 번호 결과", style = MaterialTheme.typography.titleLarge)

                if (!myNumbers.isNullOrEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.clickable {
                            onRegisterClick()
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "이번 회차 당첨 결과",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (myNumbers.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = onRegisterClick) {
                        Text("내 로또 번호 등록하기")
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))

                myNumbers.forEach {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    )  {
                        it.forEach {
                            LottoBall(number = it)
                            Spacer(Modifier.width(4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ){
                    when (result) {
                        MatchResult.BeforeDraw -> Text("추첨 전입니다.")
                        is MatchResult.Win -> Text("\uD83C\uDF89 ${result.rank}등 당첨! 축하합니다 \uD83C\uDF89", fontWeight = FontWeight.SemiBold)
                        is MatchResult.Lose -> Text("아쉽게도 낙첨되었습니다.")
                        null -> TODO()
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            RealtimeDdayText()

            Spacer(Modifier.height(16.dp))

            Text(
                text = "지난 회차 당첨 결과",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
