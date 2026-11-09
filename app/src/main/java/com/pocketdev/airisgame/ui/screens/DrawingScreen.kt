package com.pocketdev.airisgame.ui.screens

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.core.LanguageManager
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

@Composable
fun DrawingScreen(onBack: () -> Unit) {
    val density = LocalDensity.current

    var selectedColor by remember { mutableStateOf(SunnyYellow) }
    var isRainbowMode by remember { mutableStateOf(true) }
    var colorIndex by remember { mutableStateOf(0) }
    var brushRadius by remember { mutableStateOf(18f) }

    // Off-screen cached bitmap & canvas for 60/120fps painting with zero recomposition bottleneck
    var offscreenBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var offscreenCanvas by remember { mutableStateOf<android.graphics.Canvas?>(null) }
    var drawRevision by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        onDispose {
            offscreenBitmap?.recycle()
            offscreenBitmap = null
            offscreenCanvas = null
        }
    }

    val colors = listOf(
        Pair("Rainbow", Color.Transparent),
        Pair("Red", CandyCoral),
        Pair("Yellow", SunnyYellow),
        Pair("Green", MintFresh),
        Pair("Blue", SkyVibrant),
        Pair("Purple", MagicLavender),
        Pair("Pink", BubblePink)
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF233142)) // Deep night sky background makes glowing colors pop
    ) {
        val widthPx = with(density) { maxWidth.toPx() }.toInt().coerceAtLeast(1)
        val heightPx = with(density) { maxHeight.toPx() }.toInt().coerceAtLeast(1)

        // Initialize / resize bitmap matching exact screen dimensions
        if (offscreenBitmap == null || offscreenBitmap?.width != widthPx || offscreenBitmap?.height != heightPx) {
            val bmp = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
            val cv = android.graphics.Canvas(bmp)
            offscreenBitmap = bmp
            offscreenCanvas = cv
        }

        // Fast native Canvas rendering with single-pointer tracking & palm rejection
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isRainbowMode, selectedColor, brushRadius) {
                    awaitPointerEventScope {
                        while (true) {
                            // Lock onto first touch down (Single Pointer Locking / Palm Rejection)
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val activePointerId = down.id
                            var lastPoint = down.position

                            SoundEngine.playSparkle()
                            val colorToUse = if (isRainbowMode) {
                                RainbowPalette[colorIndex % RainbowPalette.size].also { colorIndex++ }
                            } else {
                                selectedColor
                            }

                            // Draw initial stroke dot
                            offscreenCanvas?.let { cv ->
                                val paint = Paint().apply {
                                    isAntiAlias = true
                                    color = colorToUse.toArgb()
                                    style = Paint.Style.FILL
                                }
                                cv.drawCircle(down.position.x, down.position.y, brushRadius, paint)
                            }
                            drawRevision++

                            var strokePoints = 0

                            // Track movement only for the active pointer; ignore accidental palm touches
                            while (true) {
                                val event = awaitPointerEvent()
                                val activeChange = event.changes.firstOrNull { it.id == activePointerId }
                                if (activeChange == null || !activeChange.pressed) {
                                    // Main finger lifted
                                    break
                                }
                                if (activeChange.positionChanged()) {
                                    val currentPos = activeChange.position
                                    offscreenCanvas?.let { cv ->
                                        val linePaint = Paint().apply {
                                            isAntiAlias = true
                                            color = colorToUse.toArgb()
                                            strokeWidth = brushRadius * 2f
                                            strokeCap = Paint.Cap.ROUND
                                            strokeJoin = Paint.Join.ROUND
                                            style = Paint.Style.STROKE
                                        }
                                        cv.drawLine(lastPoint.x, lastPoint.y, currentPos.x, currentPos.y, linePaint)
                                    }
                                    lastPoint = currentPos
                                    activeChange.consume()
                                    strokePoints++
                                    if (strokePoints % 9 == 0) {
                                        SoundEngine.playSparkle()
                                    }
                                    drawRevision++
                                }
                            }
                        }
                    }
                }
        ) {
            // Read drawRevision so Compose invalidates on each stroke efficiently
            val _rev = drawRevision
            drawIntoCanvas { canvas ->
                offscreenBitmap?.let { bmp ->
                    if (!bmp.isRecycled) {
                        canvas.nativeCanvas.drawBitmap(bmp, 0f, 0f, null)
                    }
                }
            }
        }

        // Header with Clean Canvas Action Button
        ToddlerAppHeader(
            title = LanguageManager.drawingTitle(),
            subtitle = LanguageManager.drawingSubtitle(),
            onBack = onBack,
            actionSlot = {
                AppIconButton(
                    icon = Icons.Rounded.Delete,
                    contentDescription = LanguageManager.clear(),
                    size = 44.dp,
                    iconSize = 22.dp,
                    onClick = {
                        SoundEngine.playPop()
                        SpeechEngine.speak(
                            if (LanguageManager.isMalay()) "Kanvas bersih!" else "Clean canvas!"
                        )
                        offscreenBitmap?.eraseColor(android.graphics.Color.TRANSPARENT)
                        drawRevision++
                    }
                )
            }
        )

        // Bottom Palette & Brush Selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp, start = 12.dp, end = 12.dp)
        ) {
            // Brush Size Selector Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    Pair("Nipis / Fine", 10f),
                    Pair("Sederhana / Medium", 18f),
                    Pair("Tebal / Thick", 28f)
                ).forEach { (label, r) ->
                    val isSelected = brushRadius == r
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) SunnyYellow else Color.White.copy(alpha = 0.85f))
                            .clickable {
                                SoundEngine.playSparkle()
                                brushRadius = r
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else TextDark
                        )
                    }
                }
            }

            // Color Swatches
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = 0.95f))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                colors.forEach { (name, color) ->
                    val isRainbow = name == "Rainbow"
                    val isSelected = if (isRainbow) isRainbowMode else (!isRainbowMode && selectedColor == color)

                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 42.dp else 34.dp)
                            .clip(CircleShape)
                            .background(
                                if (isRainbow) {
                                    androidx.compose.ui.graphics.Brush.sweepGradient(RainbowPalette)
                                } else {
                                    androidx.compose.ui.graphics.SolidColor(color)
                                }
                            )
                            .border(
                                width = if (isSelected) 3.5.dp else 1.dp,
                                color = if (isSelected) TextDark else Color(0xFFE2E8F0),
                                shape = CircleShape
                            )
                            .clickable {
                                SoundEngine.playSparkle()
                                if (isRainbow) {
                                    isRainbowMode = true
                                } else {
                                    isRainbowMode = false
                                    selectedColor = color
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isRainbow) {
                            Text(text = "🌈", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
