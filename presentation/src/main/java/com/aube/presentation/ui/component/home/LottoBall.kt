package com.aube.presentation.ui.component.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LottoBall(
    number: Int,
    modifier: Modifier = Modifier.size(40.dp)
) {
    val backgroundColor = when (number) {
        in 1..10 -> Color(0xFFF9C201)  // Yellow
        in 11..20 -> Color(0xFF68C6F1) // Blue
        in 21..30 -> Color(0xFFFD7172) // Red
        in 31..40 -> Color(0xFFAFD640) // Gray
        in 41..45 -> Color(0xFF4CAF50) // Green
        else -> Color.DarkGray
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LottoBallPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(7, 14, 25, 33, 42).forEach {
            LottoBall(number = it)
        }
    }
}
