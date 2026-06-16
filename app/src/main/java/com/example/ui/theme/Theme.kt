package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SpringForestGreenDark,
    secondary = SpringMintDark,
    tertiary = SpringGoldDark,
    background = SpringBgDark,
    surface = SpringSurfaceDark,
    onPrimary = SpringBgDark,
    onSecondary = SpringTextLight,
    onTertiary = SpringBgDark,
    onBackground = SpringTextLight,
    onSurface = SpringTextLight
)

private val LightColorScheme = lightColorScheme(
    primary = SpringForestGreenLight,
    secondary = SpringMintLight,
    tertiary = SpringGoldLight,
    background = SpringBgLight,
    surface = SpringSurfaceLight,
    onPrimary = SpringSurfaceLight,
    onSecondary = SpringTextDark,
    onTertiary = SpringTextDark,
    onBackground = SpringTextDark,
    onSurface = SpringTextDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color support
    dynamicColor: Boolean = false, // Set to false to force our beautiful customized Spring theme
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
