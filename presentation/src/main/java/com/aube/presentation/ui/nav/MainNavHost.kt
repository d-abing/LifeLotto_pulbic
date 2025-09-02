package com.aube.presentation.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aube.presentation.ui.screens.HomeScreen
import com.aube.presentation.ui.screens.NotificationScreen
import com.aube.presentation.ui.screens.RecommendScreen
import com.aube.presentation.ui.screens.RegisterScreen
import com.aube.presentation.viewmodel.LottoViewModel

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val lottoViewModel: LottoViewModel = hiltViewModel()

    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Recommend.route) {
            RecommendScreen(
                modifier = modifier
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                modifier = modifier,
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                onRecommendWithLuckyNumbers = {
                    navController.navigate(Screen.Recommend.route)
                },
                lottoViewModel = lottoViewModel
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                modifier = modifier,
                lottoViewModel = lottoViewModel
            )
        }
        composable(Screen.Notification.route) { NotificationScreen() }
    }
}
