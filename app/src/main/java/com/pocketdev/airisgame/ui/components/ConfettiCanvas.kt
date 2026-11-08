package com.pocketdev.airisgame.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import com.pocketdev.airisgame.ui.theme.RainbowPalette
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float,
    var rotation: Float,
    val rotationSpeed: Float,
    val shape: Int, // 0 = Circle, 1 = Ribbon, 2 = Star
    var alpha: Float = 1.0f
)

class ConfettiState {
    // Plain ArrayList avoids observable collection overhead during 60+ FPS simulation
    val particles = ArrayList<ConfettiParticle>()
    var frameTick by mutableStateOf(0L)
    var isRunning by mutableStateOf(false)

    fun hasParticles(): Boolean = particles.isNotEmpty()

    fun spawn(originX: Float, originY: Float, count: Int = 35) {
        val rand = Random(System.currentTimeMillis())
        for (i in 0 until count) {
            val angle = rand.nextDouble(0.0, 2 * PI)
            val speed = rand.nextDouble(5.0, 16.0).toFloat()
            val vx = (cos(angle) * speed).toFloat()
            val vy = (sin(angle) * speed).toFloat() - rand.nextFloat() * 7.0f // burst upwards
            val color = RainbowPalette[rand.nextInt(RainbowPalette.size)]
            val size = rand.nextFloat() * 16f + 12f
            val shape = rand.nextInt(3)
            val rotation = rand.nextFloat() * 360f
            val rotationSpeed = (rand.nextFloat() - 0.5f) * 18f

            particles.add(
                ConfettiParticle(
                    x = originX,
                    y = originY,
                    vx = vx,
                    vy = vy,
                    color = color,
                    size = size,
                    rotation = rotation,
                    rotationSpeed = rotationSpeed,
                    shape = shape
                )
            )
        }
        isRunning = true
    }

    fun onFrame(tick: Long) {
        frameTick = tick
    }

    fun updateAndDraw(drawScope: DrawScope, height: Float) {
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.x += p.vx
            p.y += p.vy
            p.vy += 0.42f // Gravity
            p.vx *= 0.98f // Drag
            p.rotation += p.rotationSpeed
            p.alpha -= 0.005f

            if (p.y > height + 60f || p.alpha <= 0.05f) {
                iterator.remove()
                continue
            }

            drawScope.rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                when (p.shape) {
                    0 -> { // Circle
                        drawCircle(
                            color = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                            radius = p.size / 2f,
                            center = Offset(p.x, p.y)
                        )
                    }
                    1 -> { // Ribbon
                        drawRect(
                            color = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                            topLeft = Offset(p.x - p.size / 2f, p.y - p.size / 4f),
                            size = Size(p.size, p.size / 2f)
                        )
                    }
                    2 -> { // Star
                        drawStar(p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)), Offset(p.x, p.y), p.size / 2f)
                    }
                }
            }
        }

        if (particles.isEmpty() && isRunning) {
            isRunning = false
        }
    }

    private fun DrawScope.drawStar(color: Color, center: Offset, radius: Float) {
        val path = Path()
        val points = 5
        val innerRadius = radius * 0.45f
        val step = (PI / points).toFloat()

        var angle = -PI.toFloat() / 2f
        path.moveTo(center.x + radius * cos(angle), center.y + radius * sin(angle))

        for (i in 0 until points) {
            angle += step
            path.lineTo(center.x + innerRadius * cos(angle), center.y + innerRadius * sin(angle))
            angle += step
            path.lineTo(center.x + radius * cos(angle), center.y + radius * sin(angle))
        }
        path.close()
        drawPath(path, color)
    }
}

@Composable
fun rememberConfettiState(): ConfettiState {
    return remember { ConfettiState() }
}

@Composable
fun ConfettiOverlay(state: ConfettiState, modifier: Modifier = Modifier) {
    LaunchedEffect(state.isRunning) {
        if (state.isRunning) {
            while (state.hasParticles()) {
                withFrameMillis { now ->
                    state.onFrame(now)
                }
            }
            state.isRunning = false
        }
    }

    if (state.hasParticles() || state.isRunning) {
        Canvas(modifier = modifier.fillMaxSize()) {
            // Read frameTick to invalidate canvas draw without triggering layout/recomposition
            @Suppress("UNUSED_VARIABLE")
            val tick = state.frameTick
            state.updateAndDraw(this, size.height)
        }
    }
}
