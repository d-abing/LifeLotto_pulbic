package com.aube.presentation.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Recommend : Screen("recommend", "번호 추천", Icons.Default.ThumbUp)
    object Home : Screen("home", "홈", Icons.Default.Home)
    object Notification : Screen("notification", "당첨 알림", Icons.Default.Notifications)
}
