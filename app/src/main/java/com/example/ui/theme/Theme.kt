package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ForestGreenLight,
    onPrimary = ForestGreenDark,
    primaryContainer = ForestGreenMedium,
    onPrimaryContainer = ForestGreenContainer,
    secondary = EarthBrownContainer,
    onSecondary = EarthBrownDark,
    secondaryContainer = EarthBrownSecondary,
    onSecondaryContainer = Color(0xFFFFDBCF),
    tertiary = OchreGold,
    onTertiary = Color(0xFF3E2E00),
    background = DarkEarthBg,
    onBackground = Color(0xFFE2E3DE),
    surface = DarkEarthSurface,
    onSurface = Color(0xFFE2E3DE),
    surfaceVariant = DarkEarthCard,
    onSurfaceVariant = Color(0xFFC2C9C0),
    outline = DarkEarthBorder,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = ForestGreenContainer,
    onPrimaryContainer = ForestGreenOnContainer,
    secondary = EarthBrownPrimary,
    onSecondary = Color.White,
    secondaryContainer = EarthBrownContainer,
    onSecondaryContainer = EarthBrownOnContainer,
    tertiary = Terracotta,
    onTertiary = Color.White,
    background = WarmStoneLight,
    onBackground = Color(0xFF1B1C1A),
    surface = WarmStoneSurface,
    onSurface = Color(0xFF1B1C1A),
    surfaceVariant = WarmStoneCard,
    onSurfaceVariant = Color(0xFF424941),
    outline = WarmStoneBorder,
    error = HazardRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // earthy brand palette preferred by default
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
