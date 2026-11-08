package com.pocketdev.airisgame.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.R
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.core.LanguageManager
import com.pocketdev.airisgame.ui.components.ConfettiOverlay
import com.pocketdev.airisgame.ui.components.RealImageOrEmoji
import com.pocketdev.airisgame.ui.components.ToddlerAppHeader
import com.pocketdev.airisgame.ui.components.ToddlerButton
import com.pocketdev.airisgame.ui.components.rememberConfettiState
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.CoralShadow
import com.pocketdev.airisgame.ui.theme.MagicLavender
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.SkyShadow
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark
import kotlin.math.hypot

data class DotThingInfo(
    val id: String,
    val enName: String,
    val bmName: String,
    val emoji: String,
    val imageRes: Int,
    val dots: List<Offset>,
    val isCurved: Boolean = true,
    val straightIndices: Set<Int> = emptySet()
)

@Composable
fun ThingsDotsScreen(onBack: () -> Unit) {
    val confettiState = rememberConfettiState()
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val isTablet = screenWidth >= 600

    var selectedIndex by remember { mutableStateOf(0) }
    var completedDotsCount by remember { mutableStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.34f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val things = remember {
        listOf(
            // 1. Birthday Cake (clean straight perimeter layers + candle)
            DotThingInfo(
                id = "cake",
                enName = "Birthday Cake",
                bmName = "Kek Hari Jadi",
                emoji = "🎂",
                imageRes = R.drawable.thing_cake,
                isCurved = false,
                dots = listOf(
                    Offset(0.18f, 0.85f), // 0: bottom left
                    Offset(0.82f, 0.85f), // 1: bottom right
                    Offset(0.78f, 0.60f), // 2: mid right
                    Offset(0.72f, 0.42f), // 3: top tier right
                    Offset(0.53f, 0.42f), // 4: candle base right
                    Offset(0.53f, 0.25f), // 5: candle top right
                    Offset(0.50f, 0.16f), // 6: candle flame tip
                    Offset(0.47f, 0.25f), // 7: candle top left
                    Offset(0.47f, 0.42f), // 8: candle base left
                    Offset(0.28f, 0.42f), // 9: top tier left
                    Offset(0.22f, 0.60f), // 10: mid left
                    Offset(0.18f, 0.85f)  // 11: close to bottom left
                )
            ),
            // 2. Ice Cream Cone (curved scoop + straight triangular cone)
            DotThingInfo(
                id = "icecream",
                enName = "Ice Cream",
                bmName = "Aiskrim",
                emoji = "🍦",
                imageRes = R.drawable.thing_icecream,
                isCurved = true,
                straightIndices = setOf(0, 7),
                dots = listOf(
                    Offset(0.50f, 0.88f), // 0: cone tip
                    Offset(0.70f, 0.52f), // 1: cone rim right
                    Offset(0.78f, 0.38f), // 2: scoop lower right
                    Offset(0.68f, 0.22f), // 3: scoop upper right
                    Offset(0.50f, 0.16f), // 4: scoop crown / cherry
                    Offset(0.32f, 0.22f), // 5: scoop upper left
                    Offset(0.22f, 0.38f), // 6: scoop lower left
                    Offset(0.30f, 0.52f), // 7: cone rim left
                    Offset(0.50f, 0.88f)  // 8: close to cone tip
                )
            ),
            // 3. Sweet Apple (curved round apple body)
            DotThingInfo(
                id = "apple",
                enName = "Sweet Apple",
                bmName = "Epal Manis",
                emoji = "🍎",
                imageRes = R.drawable.thing_apple,
                isCurved = true,
                straightIndices = setOf(0),
                dots = listOf(
                    Offset(0.50f, 0.15f), // 0: stem top
                    Offset(0.50f, 0.30f), // 1: stem base / top indent
                    Offset(0.74f, 0.34f), // 2: upper right lobe
                    Offset(0.82f, 0.62f), // 3: lower right
                    Offset(0.66f, 0.86f), // 4: bottom right
                    Offset(0.50f, 0.82f), // 5: bottom indent
                    Offset(0.34f, 0.86f), // 6: bottom left
                    Offset(0.18f, 0.62f), // 7: lower left
                    Offset(0.26f, 0.34f), // 8: upper left lobe
                    Offset(0.50f, 0.30f)  // 9: close to stem base
                )
            ),
            // 4. Teddy Bear (curved ears, cheeks, paws)
            DotThingInfo(
                id = "teddy",
                enName = "Teddy Bear",
                bmName = "Beruang Mainan",
                emoji = "🧸",
                imageRes = R.drawable.thing_teddy,
                isCurved = true,
                dots = listOf(
                    Offset(0.30f, 0.18f), // left ear
                    Offset(0.50f, 0.22f), // crown
                    Offset(0.70f, 0.18f), // right ear
                    Offset(0.75f, 0.42f), // right cheek
                    Offset(0.82f, 0.65f), // right arm
                    Offset(0.72f, 0.88f), // right foot
                    Offset(0.50f, 0.82f), // bottom middle
                    Offset(0.28f, 0.88f), // left foot
                    Offset(0.18f, 0.65f), // left arm
                    Offset(0.25f, 0.42f), // left cheek
                    Offset(0.30f, 0.18f)  // close
                )
            ),
            // 5. Floating Balloon (curved round balloon + straight string)
            DotThingInfo(
                id = "balloon",
                enName = "Balloon",
                bmName = "Belon",
                emoji = "🎈",
                imageRes = R.drawable.thing_balloon,
                isCurved = true,
                straightIndices = setOf(4, 5),
                dots = listOf(
                    Offset(0.50f, 0.15f), // 0: top
                    Offset(0.75f, 0.28f), // 1: upper right
                    Offset(0.78f, 0.52f), // 2: mid right
                    Offset(0.60f, 0.72f), // 3: lower right
                    Offset(0.50f, 0.78f), // 4: knot
                    Offset(0.48f, 0.92f), // 5: string bottom
                    Offset(0.50f, 0.78f), // 6: back to knot
                    Offset(0.40f, 0.72f), // 7: lower left
                    Offset(0.22f, 0.52f), // 8: mid left
                    Offset(0.25f, 0.28f), // 9: upper left
                    Offset(0.50f, 0.15f)  // 10: close
                )
            ),
            // 6. Pretty Flower (curved round petals)
            DotThingInfo(
                id = "flower",
                enName = "Flower",
                bmName = "Bunga",
                emoji = "🌸",
                imageRes = R.drawable.thing_flower,
                isCurved = true,
                dots = listOf(
                    Offset(0.50f, 0.15f), // top petal
                    Offset(0.75f, 0.25f), // top right petal
                    Offset(0.82f, 0.52f), // right petal
                    Offset(0.68f, 0.78f), // bottom right petal
                    Offset(0.32f, 0.78f), // bottom left petal
                    Offset(0.18f, 0.52f), // left petal
                    Offset(0.25f, 0.25f), // top left petal
                    Offset(0.50f, 0.15f)  // close
                )
            ),
            // 7. Cute Car (curved aerodynamic roof + straight wheels/base)
            DotThingInfo(
                id = "car",
                enName = "Cute Car",
                bmName = "Kereta Comel",
                emoji = "🚗",
                imageRes = R.drawable.vehicle_car,
                isCurved = true,
                straightIndices = setOf(3, 4, 5, 6),
                dots = listOf(
                    Offset(0.22f, 0.42f), // windshield front
                    Offset(0.50f, 0.36f), // roof
                    Offset(0.76f, 0.50f), // rear window
                    Offset(0.88f, 0.65f), // rear bumper
                    Offset(0.75f, 0.78f), // rear wheel
                    Offset(0.45f, 0.78f), // bottom center
                    Offset(0.25f, 0.78f), // front wheel
                    Offset(0.12f, 0.65f), // front bumper
                    Offset(0.22f, 0.42f)  // close
                )
            )
        )
    }

    val currentItem = things[selectedIndex]
    val currentDots = currentItem.dots
    val displayName = if (LanguageManager.isMalay()) currentItem.bmName else currentItem.enName

    LaunchedEffect(selectedIndex) {
        completedDotsCount = 0
        isCompleted = false
        SpeechEngine.speak(
            if (LanguageManager.isMalay())
                "Sambung titik untuk melihat $displayName! ${currentItem.emoji}"
            else
                "Connect the dots to reveal $displayName! ${currentItem.emoji}"
        )
    }

    fun handleConnectDot(dotIndex: Int) {
        if (dotIndex == completedDotsCount) {
            completedDotsCount++
            SoundEngine.playPop()
            SoundEngine.playSparkle()

            if (completedDotsCount >= currentDots.size) {
                isCompleted = true
                SoundEngine.playCelebrationFanfare()
                confettiState.spawn(300f, 300f, 50)
                SpeechEngine.speak(
                    if (LanguageManager.isMalay())
                        "Tahniah! Anda berjaya melukis $displayName! ${currentItem.emoji}"
                    else
                        "Hooray! You revealed $displayName! ${currentItem.emoji}"
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Header with language and sound controls
            ToddlerAppHeader(
                title = LanguageManager.thingsDotsTitle(),
                subtitle = LanguageManager.thingsDotsSubtitle(),
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Selector Row for Things
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(things) { index, item ->
                    val isSelected = index == selectedIndex
                    val name = if (LanguageManager.isMalay()) item.bmName else item.enName

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSelected) CandyCoral else Color.White.copy(alpha = 0.92f))
                            .border(
                                1.5.dp,
                                if (isSelected) Color.White else Color(0xFFE2E8F0),
                                RoundedCornerShape(18.dp)
                            )
                            .clickable {
                                if (index != selectedIndex) {
                                    SoundEngine.playPop()
                                    selectedIndex = index
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = item.emoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) Color.White else TextDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Connect-the-Dots Canvas / Reveal Area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Reveal real picture when completed
                    if (isCompleted) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            RealImageOrEmoji(
                                imageRes = currentItem.imageRes,
                                emoji = currentItem.emoji,
                                modifier = Modifier
                                    .size(if (isTablet) 240.dp else 190.dp)
                                    .clip(RoundedCornerShape(26.dp)),
                                emojiSize = 85.sp,
                                cornerRadius = 26.dp,
                                contentDescription = displayName
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = displayName,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextDark
                            )

                            Text(
                                text = if (LanguageManager.isMalay()) "Wah, cantiknya!" else "Wow, so wonderful!",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CandyCoral
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            ToddlerButton(
                                text = "Seterusnya ▶",
                                buttonColor = CandyCoral,
                                shadowColor = CoralShadow,
                                height = 48.dp,
                                modifier = Modifier.width(200.dp),
                                onClick = {
                                    SoundEngine.playSparkle()
                                    selectedIndex = (selectedIndex + 1) % things.size
                                }
                            )
                        }
                    }

                    // Canvas while connecting dots
                    if (!isCompleted) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                                .pointerInput(selectedIndex, completedDotsCount) {
                                    detectTapGestures { tapOffset ->
                                        val canvasW = size.width
                                        val canvasH = size.height
                                        val nextDotIdx = completedDotsCount
                                        if (nextDotIdx < currentDots.size) {
                                            val targetPt = currentDots[nextDotIdx]
                                            val actualPt = Offset(targetPt.x * canvasW, targetPt.y * canvasH)
                                            val dist = hypot(tapOffset.x - actualPt.x, tapOffset.y - actualPt.y)
                                            if (dist < 60.dp.toPx()) {
                                                handleConnectDot(nextDotIdx)
                                            }
                                        }
                                    }
                                }
                                .pointerInput(selectedIndex, completedDotsCount) {
                                    detectDragGestures { change, _ ->
                                        val canvasW = size.width
                                        val canvasH = size.height
                                        val nextDotIdx = completedDotsCount
                                        if (nextDotIdx < currentDots.size) {
                                            val targetPt = currentDots[nextDotIdx]
                                            val actualPt = Offset(targetPt.x * canvasW, targetPt.y * canvasH)
                                            val dist = hypot(change.position.x - actualPt.x, change.position.y - actualPt.y)
                                            if (dist < 60.dp.toPx()) {
                                                handleConnectDot(nextDotIdx)
                                            }
                                        }
                                    }
                                }
                        ) {
                            val w = size.width
                            val h = size.height

                            // 1. Dotted outline connecting shape
                            if (currentDots.size > 1) {
                                val guidePath = buildSmoothPath(
                                    dots = currentDots,
                                    w = w,
                                    h = h,
                                    count = currentDots.size,
                                    isCurved = currentItem.isCurved,
                                    straightIndices = currentItem.straightIndices
                                )
                                drawPath(
                                    path = guidePath,
                                    color = Color(0xFFCBD5E1),
                                    style = Stroke(
                                        width = 3.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f),
                                        cap = StrokeCap.Round
                                    )
                                )
                            }

                            // 2. Connected solid strokes
                            if (completedDotsCount > 1) {
                                val strokePath = buildSmoothPath(
                                    dots = currentDots,
                                    w = w,
                                    h = h,
                                    count = completedDotsCount,
                                    isCurved = currentItem.isCurved,
                                    straightIndices = currentItem.straightIndices
                                )
                                drawPath(
                                    path = strokePath,
                                    color = CandyCoral,
                                    style = Stroke(
                                        width = 7.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                )
                            }

                            // 3. Draw dots with numbers
                            currentDots.forEachIndexed { index, normPt ->
                                val pt = Offset(normPt.x * w, normPt.y * h)
                                val isConnected = index < completedDotsCount
                                val isNext = index == completedDotsCount

                                if (isNext) {
                                    drawCircle(
                                        color = SunnyYellow.copy(alpha = 0.55f),
                                        radius = 24.dp.toPx() * pulseScale,
                                        center = pt
                                    )
                                }

                                drawCircle(
                                    color = if (isConnected) CandyCoral
                                    else if (isNext) SunnyYellow
                                    else Color(0xFFE2E8F0),
                                    radius = if (isNext) 18.dp.toPx() else 14.dp.toPx(),
                                    center = pt
                                )

                                drawCircle(
                                    color = Color.White,
                                    radius = if (isNext) 10.dp.toPx() else 7.dp.toPx(),
                                    center = pt
                                )
                            }
                        }
                    }
                }
            }
        }

        ConfettiOverlay(state = confettiState)
    }
}

/**
 * Builds either a smooth Catmull-Rom curved path or straight polygon segments.
 * Pass straightIndices to keep specific segments straight (e.g. ice cream cone).
 */
private fun buildSmoothPath(
    dots: List<Offset>,
    w: Float,
    h: Float,
    count: Int,
    isCurved: Boolean,
    straightIndices: Set<Int> = emptySet()
): Path {
    val path = Path()
    if (dots.isEmpty() || count <= 0) return path
    val pts = dots.take(count).map { Offset(it.x * w, it.y * h) }
    if (pts.size == 1) {
        path.moveTo(pts[0].x, pts[0].y)
        return path
    }
    path.moveTo(pts[0].x, pts[0].y)
    if (!isCurved) {
        for (i in 1 until pts.size) {
            path.lineTo(pts[i].x, pts[i].y)
        }
        return path
    }

    for (i in 0 until pts.size - 1) {
        val p1 = pts[i]
        val p2 = pts[i + 1]
        if (straightIndices.contains(i)) {
            path.lineTo(p2.x, p2.y)
        } else {
            val p0 = if (i > 0) pts[i - 1] else Offset(p1.x * 2 - p2.x, p1.y * 2 - p2.y)
            val p3 = if (i + 2 < pts.size) pts[i + 2] else Offset(p2.x * 2 - p1.x, p2.y * 2 - p1.y)

            val c1x = p1.x + (p2.x - p0.x) / 6f
            val c1y = p1.y + (p2.y - p0.y) / 6f
            val c2x = p2.x - (p3.x - p1.x) / 6f
            val c2y = p2.y - (p3.y - p1.y) / 6f

            path.cubicTo(c1x, c1y, c2x, c2y, p2.x, p2.y)
        }
    }
    return path
}
