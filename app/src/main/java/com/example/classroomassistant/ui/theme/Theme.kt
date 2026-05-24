package com.example.classroomassistant.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val Light = lightColorScheme(
    primary = Sage500,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = Sage300,
    background = Sage50,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color(0xFF1B1B1B)
)

private val Dark = darkColorScheme(
    primary = Sage300,
    secondary = Sage500
)

@Composable
fun ClassroomAssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) Dark else Light,
        typography = AppTypography,
        content = content
    )
}
