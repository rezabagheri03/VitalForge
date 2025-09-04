package com.vitalforge.watch.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.vitalforge.watch.R

private val LightColors = lightColorScheme(
    primary = com.vitalforge.watch.presentation.theme.Colors.Primary,
    secondary = com.vitalforge.watch.presentation.theme.Colors.Secondary
)

private val DarkColors = darkColorScheme(
    primary = com.vitalforge.watch.presentation.theme.Colors.PrimaryDark,
    secondary = com.vitalforge.watch.presentation.theme.Colors.SecondaryDark
)

@Composable
fun VitalForgeTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = com.vitalforge.watch.presentation.theme.Typography,
        content = content
    )
}
