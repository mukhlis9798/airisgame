package com.pocketdev.airisgame.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.ui.components.AppBackButton
import com.pocketdev.airisgame.ui.components.AppIconButton
import com.pocketdev.airisgame.ui.components.ToddlerAppHeader
import com.pocketdev.airisgame.ui.theme.BubblePink
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.MagicLavender
import com.pocketdev.airisgame.ui.theme.MintFresh
import com.pocketdev.airisgame.ui.theme.RainbowPalette
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark

data class DrawPoint(
    val offset: Offset,
    val color: Color,
    val radius: Float
)

@Composable
fun DrawingScreen(onBack: () -> Unit) {
    val points = remember { mutableStateListOf<DrawPoint>() }
    var selectedColor by remember { mutableStateOf(SunnyYellow) }
    var isRainbowMode by remember { mutableStateOf(true) }
    var colorIndex by remember { mutableStateOf(0) }

    val colors = listOf(
        Pair("Rainbow", Color.Transparent),
        Pair("Red", CandyCoral),
        Pair("Yellow", SunnyYellow),
        Pair("Green", MintFresh),
        Pair("Blue", SkyVibrant),
        Pair("Purple", MagicLavender),
        Pair("Pink", BubblePink)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF233142)) // Deep night sky background makes glowing colors pop brilliantly!
    ) {
        // Drawing Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            SoundEngine.playSparkle()
                            val colorToUse = if (isRainbowMode) {
                                RainbowPalette[colorIndex % RainbowPalette.size].also { colorIndex++ }
                            } else {
                                selectedColor
                            }
                            points.add(DrawPoint(offset, colorToUse, 26f))
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val colorToUse = if (isRainbowMode) {
                                RainbowPalette[colorIndex % RainbowPalette.size].also { colorIndex++ }
                            } else {
                                selectedColor
                            }
                            points.add(DrawPoint(change.position, colorToUse, 24f))
                            if (points.size % 8 == 0) {
                                SoundEngine.playSparkle()
                            }
                        }
                    )
                }
        ) {
            for (p in points) {
                drawCircle(
                    color = p.color,
                    radius = p.radius,
                    center = p.offset
                )
            }
        }

        // Universal Header with Language Switcher and Sound Settings Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            ToddlerAppHeader(
                title = com.pocketdev.airisgame.core.LanguageManager.drawingTitle(),
                subtitle = com.pocketdev.airisgame.core.LanguageManager.drawingSubtitle(),
                onBack = onBack,
                actionSlot = {
                    AppIconButton(
                        icon = Icons.Rounded.Delete,
                        contentDescription = com.pocketdev.airisgame.core.LanguageManager.clear(),
                        size = 38.dp,
                        iconSize = 22.dp,
                        onClick = {
                            SoundEngine.playPop()
                            SpeechEngine.speak(
                                if (com.pocketdev.airisgame.core.LanguageManager.isMalay()) "Kanvas bersih!" else "Clean canvas!"
                            )
                            points.clear()
                        }
                    )
                }
            )
        }

        // Bottom Color Palette
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 12.dp, end = 12.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White.copy(alpha = 0.95f))
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            colors.forEach { (name, color) ->
                val isSelected = (name == "Rainbow" && isRainbowMode) || (!isRainbowMode && selectedColor == color)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (name == "Rainbow") {
                                androidx.compose.ui.graphics.Brush.sweepGradient(RainbowPalette)
                            } else {
                                androidx.compose.ui.graphics.SolidColor(color)
                            }
                        )
                        .clickable {
                            SoundEngine.playBoing()
                            if (name == "Rainbow") {
                                isRainbowMode = true
                                SpeechEngine.speak("Rainbow magic!")
                            } else {
                                isRainbowMode = false
                                selectedColor = color
                                SpeechEngine.speak(name)
                            }
                        }
                        .semantics { contentDescription = "Color $name" },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
            }
        }
    }
}
