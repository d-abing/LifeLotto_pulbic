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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aube.presentation.model.MatchResult
import com.aube.presentation.util.calculateNextDrawDday
import com.aube.presentation.util.formatDday

@Composable
fun MyNumberCard(
    myNumbers: List<Int>,
    result: MatchResult,
    onRegisterClick: () -> Unit = {}
) {
    val duration = remember { calculateNextDrawDday() }
    val dDayText = remember { formatDday(duration) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("내 번호 결과", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            if (myNumbers.isNullOrEmpty()) {
                Text("등록된 번호가 없어요.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onRegisterClick) {
                    Text("내 번호 등록하기")
                }
            } else {
                Row {
                    myNumbers.forEach {
                        LottoBall(number = it)
                        Spacer(Modifier.width(4.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                when (result) {
                    MatchResult.BeforeDraw -> Text("추첨 전입니다.")
                    is MatchResult.Win -> Text("${result.rank}등 당첨! 당첨금: ${result.prize}")
                    is MatchResult.Lose -> Text("낙첨되었습니다.")
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("🕒 다음 추첨까지 $dDayText", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
