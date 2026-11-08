package com.pocketdev.airisgame.ui.screens

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.ui.components.AppBackButton
import com.pocketdev.airisgame.ui.components.ConfettiOverlay
import com.pocketdev.airisgame.ui.components.ToddlerAppHeader
import com.pocketdev.airisgame.ui.components.rememberConfettiState
import com.pocketdev.airisgame.ui.theme.BubblePink
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.FreshTeal
import com.pocketdev.airisgame.ui.theme.MagicLavender
import com.pocketdev.airisgame.ui.theme.MintFresh
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark
import com.pocketdev.airisgame.ui.theme.WarmOrange
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private class BalloonPhysics(
    var x: Float,
    var y: Float,
    val radiusX: Float,
    val radiusY: Float,
    val speed: Float,
    val swayAmplitude: Float,
    val swaySpeed: Float,
    var swayPhase: Float,
    val color: Color,
    val content: String,
    val spokenName: String
)

private class BalloonBurst(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float,
    var alpha: Float = 1.0f
)

@Composable
fun BalloonsScreen(onBack: () -> Unit) {
    val confettiState = rememberConfettiState()
    var poppedCount by remember { mutableStateOf(0) }
    var frameTick by remember { mutableStateOf(0L) }

    val density = LocalDensity.current.density
    val textPaint = remember(density) {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            textSize = 34f * density
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = android.graphics.Color.WHITE
            setShadowLayer(6f * density, 0f, 2f * density, android.graphics.Color.argb(90, 0, 0, 0))
        }
    }

    val balloonColors = listOf(
        CandyCoral, SunnyYellow, MintFresh, SkyVibrant, MagicLavender, BubblePink, WarmOrange, FreshTeal
    )

    val balloonCatalog = listOf(
        Pair("1", "One!"),
        Pair("2", "Two!"),
        Pair("3", "Three!"),
        Pair("4", "Four!"),
        Pair("5", "Five!"),
        Pair("6", "Six!"),
        Pair("7", "Seven!"),
        Pair("8", "Eight!"),
        Pair("9", "Nine!"),
        Pair("10", "Ten!"),
        Pair("🍎", "Red Apple!"),
        Pair("🍌", "Yellow Banana!"),
        Pair("🍓", "Sweet Strawberry!"),
        Pair("⭐", "Golden Star!"),
        Pair("🍊", "Orange!"),
        Pair("🐱", "Kitty Cat!"),
        Pair("🐶", "Puppy Dog!"),
        Pair("🎈", "Airis Balloon!")
    )

    val balloons = remember { ArrayList<BalloonPhysics>() }
    val bursts = remember { ArrayList<BalloonBurst>() }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyGradient)
    ) {
        val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val heightPx = with(LocalDensity.current) { maxHeight.toPx() }

        // Continuous high-performance 60-120 FPS animation loop
        LaunchedEffect(widthPx, heightPx) {
            val rand = Random(System.currentTimeMillis())

            fun createBalloon(startY: Float): BalloonPhysics {
                val pick = balloonCatalog[rand.nextInt(balloonCatalog.size)]
                val col = balloonColors[rand.nextInt(balloonColors.size)]
                val rx = rand.nextFloat() * 10f + 48f * density
                val ry = rx * 1.18f
                return BalloonPhysics(
                    x = rand.nextFloat() * (widthPx - rx * 2.5f).coerceAtLeast(10f) + rx,
                    y = startY,
                    radiusX = rx,
                    radiusY = ry,
                    speed = rand.nextFloat() * 1.8f * density + 1.6f * density,
                    swayAmplitude = rand.nextFloat() * 20f + 12f,
                    swaySpeed = rand.nextFloat() * 0.04f + 0.02f,
                    swayPhase = rand.nextFloat() * 2 * PI.toFloat(),
                    color = col,
                    content = pick.first,
                    spokenName = pick.second
                )
            }

            // Populate balloons initially
            if (balloons.isEmpty()) {
                for (i in 0 until 7) {
                    balloons.add(createBalloon(heightPx + i * (heightPx / 6f)))
                }
            }

            var lastTime = System.nanoTime()
            while (true) {
                withFrameNanos { now ->
                    val dt = ((now - lastTime) / 1_000_000_000f).coerceIn(0.001f, 0.04f)
                    lastTime = now

                    // Update balloons
                    val it = balloons.iterator()
                    while (it.hasNext()) {
                        val b = it.next()
                        b.y -= b.speed * (dt * 60f)
                        b.swayPhase += b.swaySpeed * (dt * 60f)
                        b.x += sin(b.swayPhase) * 1.1f

                        // Recycle if floated off top of screen
                        if (b.y + b.radiusY < -50f) {
                            it.remove()
                        }
                    }

                    // Replenish to maintain 7 cheerful balloons on screen
                    while (balloons.size < 7) {
                        balloons.add(createBalloon(heightPx + rand.nextFloat() * 120f * density))
                    }

                    // Update popping burst particles
                    val burstIt = bursts.iterator()
                    while (burstIt.hasNext()) {
                        val p = burstIt.next()
                        p.x += p.vx * (dt * 60f)
                        p.y += p.vy * (dt * 60f)
                        p.vy += 0.35f * (dt * 60f)
                        p.alpha -= 0.025f * (dt * 60f)
                        if (p.alpha <= 0.05f) {
                            burstIt.remove()
                        }
                    }

                    frameTick = now
                }
            }
        }

        // Hardware-Accelerated Canvas Rendering
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val rand = Random(System.currentTimeMillis())
                        // Check collisions in reverse order (top balloon gets hit first)
                        var hitIndex = -1
                        for (i in balloons.indices.reversed()) {
                            val b = balloons[i]
                            val dx = tapOffset.x - b.x
                            val dy = tapOffset.y - b.y
                            // Elliptical hit test with generous toddler margin
                            val hitRadiusX = b.radiusX + 18f * density
                            val hitRadiusY = b.radiusY + 18f * density
                            if ((dx * dx) / (hitRadiusX * hitRadiusX) + (dy * dy) / (hitRadiusY * hitRadiusY) <= 1.0f) {
                                hitIndex = i
                                break
                            }
                        }

                        if (hitIndex != -1) {
                            val popped = balloons.removeAt(hitIndex)
                            poppedCount++
                            SoundEngine.playPop()
                            SpeechEngine.speak(popped.spokenName)

                            // Spawn burst shards directly in physics
                            for (p in 0 until 14) {
                                val angle = rand.nextDouble(0.0, 2 * PI)
                                val speed = rand.nextDouble(4.0, 14.0).toFloat() * density
                                bursts.add(
                                    BalloonBurst(
                                        x = popped.x,
                                        y = popped.y,
                                        vx = (cos(angle) * speed).toFloat(),
                                        vy = (sin(angle) * speed).toFloat(),
                                        color = popped.color,
                                        size = rand.nextFloat() * 10f * density + 6f * density
                                    )
                                )
                            }

                            // Confetti burst
                            confettiState.spawn(popped.x, popped.y, 22)

                            // Milestone celebration every 5 pops
                            if (poppedCount % 5 == 0) {
                                SoundEngine.playCelebrationFanfare()
                                SpeechEngine.speak(
                                    if (com.pocketdev.airisgame.core.LanguageManager.isMalay())
                                        "Hebat! $poppedCount belon meletup!"
                                    else
                                        "Hooray! $poppedCount balloons popped!"
                                )
                            }
                        }
                    }
                }
        ) {
            // Read frameTick to trigger redraw at monitor refresh rate without layout pass
            @Suppress("UNUSED_VARIABLE")
            val tick = frameTick

            // Draw all balloons
            for (b in balloons) {
                val cx = b.x
                val cy = b.y
                val rx = b.radiusX
                val ry = b.radiusY

                // 1. Balloon String
                val stringPath = Path().apply {
                    moveTo(cx, cy + ry)
                    val sway = sin(b.swayPhase * 1.5f) * 14f * density
                    quadraticBezierTo(
                        cx + sway, cy + ry + 22f * density,
                        cx, cy + ry + 48f * density
                    )
                }
                drawPath(
                    path = stringPath,
                    color = Color.White.copy(alpha = 0.75f),
                    style = Stroke(width = 2.2f * density, cap = StrokeCap.Round)
                )

                // 2. Balloon Knot
                val knotPath = Path().apply {
                    moveTo(cx - 7f * density, cy + ry + 6f * density)
                    lineTo(cx + 7f * density, cy + ry + 6f * density)
                    lineTo(cx, cy + ry - 2f * density)
                    close()
                }
                drawPath(knotPath, color = b.color)

                // 3. Balloon 3D Body (Radial Gradient)
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            b.color.copy(alpha = 1.0f),
                            b.color,
                            b.color.copy(alpha = 0.85f),
                            Color.Black.copy(alpha = 0.15f)
                        ),
                        center = Offset(cx - rx * 0.28f, cy - ry * 0.28f),
                        radius = rx * 1.25f
                    ),
                    topLeft = Offset(cx - rx, cy - ry),
                    size = Size(rx * 2f, ry * 2f)
                )

                // 4. Glossy Specular Highlight Arc
                drawOval(
                    color = Color.White.copy(alpha = 0.45f),
                    topLeft = Offset(cx - rx * 0.65f, cy - ry * 0.70f),
                    size = Size(rx * 0.38f, ry * 0.52f)
                )

                // 5. Centered Number / Emoji / Letter
                // Adjust text baseline so it aligns centered vertically
                val metrics = textPaint.fontMetrics
                val textBaseline = cy - (metrics.ascent + metrics.descent) / 2f
                drawContext.canvas.nativeCanvas.drawText(
                    b.content,
                    cx,
                    textBaseline,
                    textPaint
                )
            }

            // Draw bursting shards
            for (p in bursts) {
                drawCircle(
                    color = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                    radius = p.size / 2f,
                    center = Offset(p.x, p.y)
                )
            }
        }

        // Universal Header with Language Switcher and Sound Control Dialog
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            ToddlerAppHeader(
                title = com.pocketdev.airisgame.core.LanguageManager.balloonsTitle(),
                subtitle = com.pocketdev.airisgame.core.LanguageManager.balloonsSubtitle(),
                onBack = onBack,
                actionSlot = {
                    Box(
                        modifier = Modifier
                            .height(38.dp)
                            .clip(RoundedCornerShape(19.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(19.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎈 $poppedCount",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDark
                        )
                    }
                }
            )
        }

        ConfettiOverlay(state = confettiState)
    }
}
