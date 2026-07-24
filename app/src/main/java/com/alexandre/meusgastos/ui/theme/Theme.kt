package com.alexandre.meusgastos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2F6D4F),
    secondary = Color(0xFF4C6EF5),
    tertiary = Color(0xFFF08C00)
)
private val DarkColors = darkColorScheme(
    primary = Color(0xFF7FCBA3),
    secondary = Color(0xFF91A7FF),
    tertiary = Color(0xFFFFC078)
)

@Composable
fun MeusGastosTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
