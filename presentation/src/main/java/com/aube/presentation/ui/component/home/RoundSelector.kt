package com.aube.presentation.ui.component.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundSelector(
    latestRound: Int,
    selectedRound: Int,
    date: String,
    onRoundSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val rounds = remember(latestRound) {
        (latestRound downTo (latestRound - 20).coerceAtLeast(1)).toList()
    }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val latestDate = remember(date) {
        date.takeIf { it.isNotBlank() }?.let {
            LocalDate.parse(it, formatter)
        }
    }

    val dates = remember(latestDate) {
        if (latestDate != null) {
            (0 until 21).map { offset ->
                latestDate.minusWeeks(offset.toLong()).format(formatter)
            }
        } else {
            emptyList()
        }
    }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${selectedRound}회 당첨 결과 (${date})",
                style = MaterialTheme.typography.titleLarge
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "회차 선택",
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 4.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentWidth()
        ) {
            rounds.forEachIndexed { idx, round ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${round}회 (${dates.getOrNull(idx) ?: "날짜 없음"})",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = {
                        onRoundSelect(round)
                        expanded = false
                    }
                )
            }
        }
    }
}

