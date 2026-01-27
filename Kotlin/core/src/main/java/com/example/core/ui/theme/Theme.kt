package com.example.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val DarkColorScheme = darkColorScheme(
    surfaceContainer = CardItemBackgroundDark,
    onBackground = WhiteSoft,
    surfaceVariant = DarkOverLay,
    surfaceDim = DarkBorder,
    surfaceContainerLowest = BackgroundBehindButtonDark,
    surfaceContainerLow = BackgroundColorInput,
    surface = BackgroundNavDarkDark
)

private val LightColorScheme = lightColorScheme(
    surfaceContainer = CardItemBackgroundLight,
    onBackground = Black,
    surfaceVariant = LightOverlay,
    surfaceDim = WhiteSoft,
    surfaceContainerLowest = BackgroundBehindButtonLight,
    surfaceContainerLow = BackgroundColorInputLight,
    surface = BackgroundNavDarkLight

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun NuvioFrontendTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}