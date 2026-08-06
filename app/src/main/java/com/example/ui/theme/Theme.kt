package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkBackground = Color(0xFF090614)
val DarkSurface = Color(0xFF130D26)
val DarkSurfaceVariant = Color(0xFF1E153B)
val CyberWhite = Color(0xFFFFFFFF)
val CyberAccent = Color(0xFFC084FC) // Neon Purple Glow
val CyberAccentPrimary = Color(0xFFA855F7) // Rich Purple Accent
val CyberGreen = Color(0xFF10B981) // Undetect Emerald Green
val CyberRed = Color(0xFFF43F5E) // Neon Red/Pink
val DarkTextMuted = Color(0xFF9CA3AF)
val NeonPurpleBorder = Color(0xFF8B5CF6)

private val InjectorColorScheme = darkColorScheme(
    primary = CyberAccentPrimary,
    onPrimary = Color.White,
    secondary = CyberAccent,
    onSecondary = Color.Black,
    tertiary = CyberGreen,
    error = CyberRed,
    background = DarkBackground,
    onBackground = CyberWhite,
    surface = DarkSurface,
    onSurface = CyberWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = CyberWhite
)

@Composable
fun InjectorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = InjectorColorScheme,
        typography = Typography,
        content = content
    )
}

