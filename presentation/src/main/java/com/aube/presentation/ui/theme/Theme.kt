package com.aube.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.aube.presentation.ui.theme.OnBackgroundColor
import com.aube.presentation.ui.theme.OnSurfaceColor
import com.aube.presentation.ui.theme.PrimaryContainer
import com.aube.presentation.ui.theme.PrimaryMain
import com.aube.presentation.ui.theme.SecondaryContainer
import com.aube.presentation.ui.theme.SecondaryMain
import com.aube.presentation.ui.theme.SurfaceColor
import com.aube.presentation.ui.theme.TertiaryContainer
import com.aube.presentation.ui.theme.TertiaryMain

private val LightColorScheme = lightColorScheme(
    primary = PrimaryMain,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,

    secondary = SecondaryMain,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryContainer,

    tertiary = TertiaryMain,
    onTertiary = Color.Black,
    tertiaryContainer = TertiaryContainer,

    background = SurfaceColor,
    onBackground = OnBackgroundColor,
    surface = SurfaceColor,
    onSurface = OnSurfaceColor,

    error = Error
)

@Composable
fun LifeLottoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
