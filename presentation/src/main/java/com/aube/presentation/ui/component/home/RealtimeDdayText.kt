package com.aube.presentation.ui.component.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aube.presentation.util.calculateNextDrawDuration
import com.aube.presentation.util.formatDday
import kotlinx.coroutines.delay

@Composable
fun RealtimeDdayText() {
    var duration by remember { mutableStateOf(calculateNextDrawDuration()) }

    val currentDuration by rememberUpdatedState(duration)

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            duration = calculateNextDrawDuration()
        }
    }

    val dDayText = formatDday(currentDuration)

    Column {
        Text("🕒 다음 추첨까지 $dDayText", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(4.dp))
        Text("당첨 결과는 바로 반영되지 않을 수 있습니다.", style = MaterialTheme.typography.bodySmall)
    }
}
