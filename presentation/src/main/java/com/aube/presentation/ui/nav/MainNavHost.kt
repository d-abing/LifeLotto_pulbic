package com.aube.presentation.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aube.presentation.ui.screens.HomeScreen
import com.aube.presentation.ui.screens.NotificationScreen
import com.aube.presentation.ui.screens.RecommendScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Recommend.route) { RecommendScreen() }
        composable(Screen.Home.route) {
            HomeScreen(){
                navController.navigate(Screen.Notification.route)
            }
        }
        composable(Screen.Notification.route) { NotificationScreen() }
    }
}
