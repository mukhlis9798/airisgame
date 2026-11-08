package com.pocketdev.airisgame.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = SkyVibrant,
    onPrimary = TextLight,
    secondary = SunnyYellow,
    onSecondary = TextDark,
    tertiary = BubblePink,
    background = SoftBackground,
    surface = CardCream,
    onSurface = TextDark
)

val ToddlerShapes = Shapes(
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(40.dp)
)

@Composable
fun AirisGameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        shapes = ToddlerShapes,
        content = content
    )
}
