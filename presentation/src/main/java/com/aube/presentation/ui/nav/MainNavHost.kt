package com.aube.presentation.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aube.presentation.ui.screens.HomeScreen
import com.aube.presentation.ui.screens.NotificationScreen
import com.aube.presentation.ui.screens.RecommendScreen
import com.aube.presentation.ui.screens.RegisterScreen
import com.aube.presentation.ui.screens.StatisticsScreen
import com.aube.presentation.viewmodel.LottoViewModel

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val lottoViewModel: LottoViewModel = hiltViewModel()

    NavHost(navController, startDestination = Screen.Home.route) {
        composable(
            route = Screen.Recommend.route,
            arguments = listOf(navArgument("numbers") { type = NavType.StringType })
        ) { backStackEntry ->
            val numbers = backStackEntry.arguments?.getString("numbers")

            RecommendScreen(
                numbers =
                    if (numbers == "{numbers}") null
                    else numbers?.split(",")?.mapNotNull { it.toIntOrNull() },
                modifier = modifier,
                onStatisticsClick = {
                    navController.navigate(Screen.Statistics.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                modifier = modifier,
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                modifier = modifier,
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                onRecommendWithLuckyNumbers = { luckyNumbers ->
                    navController.navigate(Screen.Recommend.createRoute(luckyNumbers)) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
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
