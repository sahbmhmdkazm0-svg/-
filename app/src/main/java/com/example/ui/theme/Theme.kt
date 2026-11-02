package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreenLight,
    onPrimary = Color.White,
    primaryContainer = EmeraldGreenDark,
    onPrimaryContainer = EmeraldGreenContainer,
    secondary = TrophyGold,
    onSecondary = Color.Black,
    secondaryContainer = TrophyGoldDark,
    onSecondaryContainer = TrophyGoldLight,
    tertiary = TrophyGoldLight,
    onTertiary = Color.Black,
    background = StadiumDarkBackground,
    onBackground = StadiumDarkTextPrimary,
    surface = StadiumDarkSurface,
    onSurface = StadiumDarkTextPrimary,
    surfaceVariant = StadiumDarkSurfaceVariant,
    onSurfaceVariant = StadiumDarkTextSecondary,
    outline = Color(0xFF2C4436)
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreenPrimary,
    onPrimary = OnEmeraldGreen,
    primaryContainer = EmeraldGreenContainer,
    onPrimaryContainer = EmeraldGreenDark,
    secondary = TrophyGoldDark,
    onSecondary = Color.White,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = Color(0xFF4A3800),
    tertiary = TrophyGold,
    onTertiary = Color.Black,
    background = StadiumLightBackground,
    onBackground = StadiumLightTextPrimary,
    surface = StadiumLightSurface,
    onSurface = StadiumLightTextPrimary,
    surfaceVariant = StadiumLightSurfaceVariant,
    onSurfaceVariant = StadiumLightTextSecondary,
    outline = Color(0xFFCCD9D0)
)

@Composable
fun TournaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Enforce Arabic RTL layout across the entire application as requested
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
