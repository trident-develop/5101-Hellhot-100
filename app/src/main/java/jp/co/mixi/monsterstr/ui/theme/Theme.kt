package jp.co.mixi.monsterstr.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JokerColorScheme = darkColorScheme(
    primary = JokerPurple,
    onPrimary = JokerOnSurface,
    primaryContainer = JokerPurpleDeep,
    onPrimaryContainer = JokerOnSurface,
    secondary = JokerGreen,
    onSecondary = JokerInk,
    secondaryContainer = JokerGreenDeep,
    onSecondaryContainer = JokerOnSurface,
    tertiary = JokerRed,
    onTertiary = JokerOnSurface,
    tertiaryContainer = JokerRedDeep,
    onTertiaryContainer = JokerOnSurface,
    background = JokerInk,
    onBackground = JokerOnSurface,
    surface = JokerCard,
    onSurface = JokerOnSurface,
    surfaceVariant = JokerPurpleDeep,
    onSurfaceVariant = JokerOnSurface,
    outline = JokerCardEdge,
)

@Composable
fun Hellhot100Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JokerColorScheme,
        typography = Typography,
        content = content,
    )
}
