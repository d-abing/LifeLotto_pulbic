package com.aube.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.aube.presentation.ui.component.AdaptiveBanner
import com.aube.presentation.ui.nav.LifeLottoBottomBar
import com.aube.presentation.ui.nav.MainNavHost
import com.aube.presentation.ui.nav.Screen
import com.aube.presentation.ui.theme.LifeLottoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeLottoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LifeLottoApp()
                }
            }
        }
    }
}

@Composable
fun LifeLottoApp() {
    SetStatusBarColor(color = Color.White, darkIcons = true)

    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        bottomBar = {
            Column {
                AdaptiveBanner(
                    modifier = Modifier
                        .fillMaxWidth(),
                    adUnitId = stringResource(R.string.ad_unit_id_banner)
                )
                LifeLottoBottomBar(
                    navController = navController,
                    items = listOf(
                        Screen.Recommend,
                        Screen.Home,
                        Screen.Notification
                    )
                )
            }
        }
    ) { paddingValues ->
        MainNavHost(
            navController = navController,
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        )
    }
}

@Composable
fun SetStatusBarColor(
    color: Color = Color.White,
    darkIcons: Boolean = true
) {
    val activity = LocalActivity.current!!

    SideEffect {
        val window = activity.window
        window.statusBarColor = color.toArgb()
        WindowCompat.getInsetsController(window, activity.window.decorView)
            .isAppearanceLightStatusBars = darkIcons
    }
}
