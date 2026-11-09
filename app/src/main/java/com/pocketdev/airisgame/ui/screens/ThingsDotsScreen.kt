package com.pocketdev.airisgame.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.R
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.core.LanguageManager
import com.pocketdev.airisgame.ui.components.ConfettiOverlay
import com.pocketdev.airisgame.ui.components.ToddlerAppHeader
import com.pocketdev.airisgame.ui.components.ToddlerButton
import com.pocketdev.airisgame.ui.components.rememberConfettiState
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.CoralShadow
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark
import kotlin.math.hypot

data class CartoonThingInfo(
    val id: String,
    val enName: String,
    val bmName: String,
    val emoji: String,
    val imageRes: Int,
    val strokes: List<List<Offset>>,
    val strokeColor: Color,
    val playSound: () -> Unit
)

@Composable
fun ThingsDotsScreen(onBack: () -> Unit) {
    val confettiState = rememberConfettiState()
    val haptic = LocalHapticFeedback.current
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val isTablet = screenWidth >= 600

    var selectedIndex by remember { mutableStateOf(0) }
    var currentStrokeIndex by remember { mutableStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }

    val userLiveTrace = remember { mutableStateListOf<Offset>() }
    val coveredPoints = remember { mutableStateListOf<Int>() }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val things = remember { getCartoonThings() }
    val currentItem = things[selectedIndex.coerceIn(0, things.lastIndex)]
    val activeStrokes = currentItem.strokes

    // Spring bounce reveal transition when tracing completes
    val revealScale by animateFloatAsState(
        targetValue = if (isCompleted) 1.0f else 0.0f,
        animationSpec = spring(
            dampingRatio = 0.55f,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    LaunchedEffect(selectedIndex) {
        currentStrokeIndex = 0
        isCompleted = false
        userLiveTrace.clear()
        coveredPoints.clear()

        val name = if (LanguageManager.isMalay()) currentItem.bmName else currentItem.enName
        SpeechEngine.speak(name)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with unified responsive Row (Back, Title, Lang, Sound, Lock)
            ToddlerAppHeader(
                title = LanguageManager.thingsDotsTitle(),
                subtitle = LanguageManager.thingsDotsSubtitle(),
                onBack = onBack
            )

            // Horizontal Selector Carousel for all 5 Cartoon Things
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(things) { index, item ->
                    val isSelected = index == selectedIndex
                    val itemName = if (LanguageManager.isMalay()) item.bmName else item.enName

                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                if (isSelected) currentItem.strokeColor
                                else Color.White.copy(alpha = 0.94f)
                            )
                            .border(
                                1.5.dp,
                                if (isSelected) Color.White else Color(0xFFE2E8F0),
                                RoundedCornerShape(22.dp)
                            )
                            .clickable {
                                if (index != selectedIndex) {
                                    SoundEngine.playPop()
                                    selectedIndex = index
                                }
                            }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = item.emoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = itemName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) Color.White else TextDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // CENTER: Tracing Card with Recognizable Cartoon Line-Art & Spring Bounce Reveal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Tracing Canvas with Dashed Line-Art to Solid Strokes
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .pointerInput(selectedIndex, currentStrokeIndex, isCompleted) {
                                if (isCompleted) return@pointerInput

                                awaitPointerEventScope {
                                    while (true) {
                                        val down = awaitFirstDown(requireUnconsumed = false)
                                        val activePointerId = down.id
                                        val canvasW = size.width
                                        val canvasH = size.height

                                        if (currentStrokeIndex >= activeStrokes.size) break
                                        val targetStroke = activeStrokes[currentStrokeIndex]
                                        val hitTolerancePx = 40.dp.toPx()

                                        fun processTouch(pos: Offset) {
                                            userLiveTrace.add(pos)
                                            targetStroke.forEachIndexed { ptIdx, normPt ->
                                                val actualPt = Offset(normPt.x * canvasW, normPt.y * canvasH)
                                                val dist = hypot(pos.x - actualPt.x, pos.y - actualPt.y)
                                                if (dist < hitTolerancePx && !coveredPoints.contains(ptIdx)) {
                                                    coveredPoints.add(ptIdx)
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                }
                                            }

                                            val isStrokeComplete = (coveredPoints.size.toFloat() / targetStroke.size) >= 0.70f ||
                                                    (coveredPoints.contains(0) && coveredPoints.contains(targetStroke.lastIndex) && coveredPoints.size >= 2)

                                            if (isStrokeComplete) {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                SoundEngine.playSparkle()
                                                userLiveTrace.clear()
                                                coveredPoints.clear()
                                                currentStrokeIndex++

                                                if (currentStrokeIndex >= activeStrokes.size) {
                                                    isCompleted = true
                                                    currentItem.playSound()
                                                    confettiState.spawn(300f, 300f, 50)
                                                    val itemName = if (LanguageManager.isMalay()) currentItem.bmName else currentItem.enName
                                                    SpeechEngine.speak(
                                                        if (LanguageManager.isMalay())
                                                            "Hebat! Kamu telah melukis $itemName! ${currentItem.emoji}"
                                                        else
                                                            "Awesome! You drew $itemName! ${currentItem.emoji}"
                                                    )
                                                }
                                            }
                                        }

                                        processTouch(down.position)

                                        while (true) {
                                            val event = awaitPointerEvent()
                                            val change = event.changes.firstOrNull { it.id == activePointerId }
                                            if (change == null || !change.pressed) {
                                                userLiveTrace.clear()
                                                break
                                            }
                                            if (change.positionChanged()) {
                                                processTouch(change.position)
                                                change.consume()
                                            }
                                            if (isCompleted || currentStrokeIndex >= activeStrokes.size) {
                                                break
                                            }
                                        }
                                    }
                                }
                            }
                    ) {
                        val w = size.width
                        val h = size.height

                        // 1. FAINT SILHOUETTE STENCIL UNDERNEATH (alpha = 0.08f)
                        activeStrokes.forEach { strokePts ->
                            if (strokePts.size > 1) {
                                val stencilPath = Path().apply {
                                    moveTo(strokePts[0].x * w, strokePts[0].y * h)
                                    for (i in 1 until strokePts.size) {
                                        lineTo(strokePts[i].x * w, strokePts[i].y * h)
                                    }
                                }
                                drawPath(
                                    path = stencilPath,
                                    color = Color(0xFF64748B).copy(alpha = 0.12f),
                                    style = Stroke(
                                        width = 16.dp.toPx(),
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }

                        // 2. UNTRACED CARTOON STROKES AS PLAYFUL DASHED OUTLINES
                        activeStrokes.forEachIndexed { strokeIdx, strokePts ->
                            if (strokeIdx >= currentStrokeIndex && strokePts.size > 1) {
                                val dashedPath = Path().apply {
                                    moveTo(strokePts[0].x * w, strokePts[0].y * h)
                                    for (i in 1 until strokePts.size) {
                                        lineTo(strokePts[i].x * w, strokePts[i].y * h)
                                    }
                                }
                                drawPath(
                                    path = dashedPath,
                                    color = Color(0xFFCBD5E1),
                                    style = Stroke(
                                        width = 10.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f), 0f),
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }

                        // 3. COMPLETED CARTOON STROKES LOCKED INTO SOLID VIBRANT LINES
                        activeStrokes.forEachIndexed { strokeIdx, strokePts ->
                            if (strokeIdx < currentStrokeIndex && strokePts.size > 1) {
                                val solidPath = Path().apply {
                                    moveTo(strokePts[0].x * w, strokePts[0].y * h)
                                    for (i in 1 until strokePts.size) {
                                        lineTo(strokePts[i].x * w, strokePts[i].y * h)
                                    }
                                }
                                drawPath(
                                    path = solidPath,
                                    color = currentItem.strokeColor,
                                    style = Stroke(
                                        width = 18.dp.toPx(),
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }

                        // 4. ACTIVE LIVE TRACE DRAWN REAL-TIME WITH FORGIVING HIT-BOX
                        if (userLiveTrace.size > 1 && currentStrokeIndex < activeStrokes.size) {
                            val livePath = Path().apply {
                                moveTo(userLiveTrace[0].x, userLiveTrace[0].y)
                                for (i in 1 until userLiveTrace.size) {
                                    lineTo(userLiveTrace[i].x, userLiveTrace[i].y)
                                }
                            }
                            drawPath(
                                path = livePath,
                                color = currentItem.strokeColor.copy(alpha = 0.85f),
                                style = Stroke(
                                    width = 18.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }

                        // 5. PULSATING START GUIDE BADGE FOR CURRENT STROKE
                        if (!isCompleted && currentStrokeIndex < activeStrokes.size) {
                            val targetStroke = activeStrokes[currentStrokeIndex]
                            if (targetStroke.isNotEmpty()) {
                                val startPt = Offset(targetStroke[0].x * w, targetStroke[0].y * h)
                                drawCircle(
                                    color = SunnyYellow.copy(alpha = 0.55f),
                                    radius = 24.dp.toPx() * pulseScale,
                                    center = startPt
                                )
                                drawCircle(
                                    color = SunnyYellow,
                                    radius = 16.dp.toPx(),
                                    center = startPt
                                )
                            }
                        }
                    }

                    // 6. SPRING BOUNCE REVEAL: FULLY COLORED CARTOON ILLUSTRATION ON COMPLETION
                    if (isCompleted && revealScale > 0.01f) {
                        Box(
                            modifier = Modifier
                                .size(if (isTablet) 300.dp else 230.dp)
                                .scale(revealScale)
                                .clip(RoundedCornerShape(32.dp))
                                .background(Color.White.copy(alpha = 0.96f))
                                .border(3.dp, SunnyYellow, RoundedCornerShape(32.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = currentItem.imageRes),
                                contentDescription = currentItem.enName,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Bottom Floating Banner when Completed
                    if (isCompleted) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(Color.White.copy(alpha = 0.96f))
                                .border(2.dp, SunnyYellow, RoundedCornerShape(22.dp))
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = currentItem.emoji, fontSize = 34.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        val itemName = if (LanguageManager.isMalay()) currentItem.bmName else currentItem.enName
                                        Text(
                                            text = "$itemName!",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TextDark
                                        )
                                        Text(
                                            text = if (LanguageManager.isMalay()) "Tahniah! Lukisan siap!" else "Awesome! Drawing complete!",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextDark.copy(alpha = 0.65f)
                                        )
                                    }
                                }

                                ToddlerButton(
                                    text = if (LanguageManager.isMalay()) "Seterusnya" else "Next",
                                    icon = "➡️",
                                    buttonColor = currentItem.strokeColor,
                                    shadowColor = CoralShadow,
                                    onClick = {
                                        SoundEngine.playBoing()
                                        selectedIndex = (selectedIndex + 1) % things.size
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Celebration Confetti
        ConfettiOverlay(state = confettiState)
    }
}

/**
 * 5 Distinct, recognizable cartoon line-art silhouettes:
 * 1. Flower: Center circle, 5 distinct rounded petals, stem, leaves
 * 2. Cute Car: Car body silhouette with wheels, headlights, windows
 * 3. Teddy Bear: Distinct head, circular ears, snout, body, arms
 * 4. Balloon: Oval balloon, knot, squiggly string
 * 5. Birthday Cake: Tiered cake silhouette, candles, flame
 */
private fun getCartoonThings(): List<CartoonThingInfo> {
    return listOf(
        // 1. FLOWER (Center circle, 5 distinct rounded petals, stem, leaves)
        CartoonThingInfo(
            id = "flower",
            enName = "Pretty Flower",
            bmName = "Bunga Cantik",
            emoji = "🌸",
            imageRes = R.drawable.thing_flower,
            strokeColor = CandyCoral,
            playSound = { SoundEngine.playChime() },
            strokes = listOf(
                // Stroke 0: Center Circle
                listOf(
                    Offset(0.50f, 0.30f), Offset(0.56f, 0.32f), Offset(0.58f, 0.38f),
                    Offset(0.56f, 0.44f), Offset(0.50f, 0.46f), Offset(0.44f, 0.44f),
                    Offset(0.42f, 0.38f), Offset(0.44f, 0.32f), Offset(0.50f, 0.30f)
                ),
                // Stroke 1: 5 Distinct Rounded Petals Outer Silhouette
                listOf(
                    Offset(0.50f, 0.30f), Offset(0.44f, 0.20f), Offset(0.50f, 0.14f), Offset(0.56f, 0.20f), Offset(0.56f, 0.32f), // Top petal
                    Offset(0.68f, 0.26f), Offset(0.74f, 0.34f), Offset(0.68f, 0.42f), Offset(0.58f, 0.42f), // Top-right petal
                    Offset(0.68f, 0.48f), Offset(0.64f, 0.58f), Offset(0.54f, 0.56f), Offset(0.52f, 0.46f), // Bottom-right petal
                    Offset(0.44f, 0.56f), Offset(0.36f, 0.58f), Offset(0.32f, 0.48f), Offset(0.42f, 0.42f), // Bottom-left petal
                    Offset(0.32f, 0.40f), Offset(0.26f, 0.32f), Offset(0.32f, 0.24f), Offset(0.44f, 0.20f), Offset(0.50f, 0.30f)  // Top-left petal & close
                ),
                // Stroke 2: Stem
                listOf(
                    Offset(0.50f, 0.46f), Offset(0.50f, 0.60f), Offset(0.49f, 0.74f), Offset(0.50f, 0.88f)
                ),
                // Stroke 3: Left Leaf
                listOf(
                    Offset(0.50f, 0.68f), Offset(0.40f, 0.62f), Offset(0.30f, 0.64f), Offset(0.36f, 0.72f), Offset(0.50f, 0.72f)
                ),
                // Stroke 4: Right Leaf
                listOf(
                    Offset(0.50f, 0.60f), Offset(0.60f, 0.54f), Offset(0.70f, 0.56f), Offset(0.64f, 0.64f), Offset(0.50f, 0.64f)
                )
            )
        ),

        // 2. CUTE CAR (Body silhouette, wheels, headlights, windows)
        CartoonThingInfo(
            id = "car",
            enName = "Cute Car",
            bmName = "Kereta Comel",
            emoji = "🚗",
            imageRes = R.drawable.vehicle_car,
            strokeColor = SkyVibrant,
            playSound = { SoundEngine.playCarHonk() },
            strokes = listOf(
                // Stroke 0: Car Body Silhouette with Roof, Hood & Wheel Well Cutouts
                listOf(
                    Offset(0.14f, 0.68f), Offset(0.24f, 0.68f), Offset(0.32f, 0.54f), Offset(0.44f, 0.36f),
                    Offset(0.64f, 0.36f), Offset(0.74f, 0.52f), Offset(0.85f, 0.60f), Offset(0.86f, 0.70f),
                    Offset(0.76f, 0.70f), Offset(0.74f, 0.64f), Offset(0.64f, 0.64f), Offset(0.62f, 0.70f),
                    Offset(0.38f, 0.70f), Offset(0.36f, 0.64f), Offset(0.26f, 0.64f), Offset(0.24f, 0.70f),
                    Offset(0.14f, 0.68f)
                ),
                // Stroke 1: Front Circular Wheel
                listOf(
                    Offset(0.30f, 0.64f), Offset(0.36f, 0.70f), Offset(0.30f, 0.78f), Offset(0.24f, 0.70f), Offset(0.30f, 0.64f)
                ),
                // Stroke 2: Rear Circular Wheel
                listOf(
                    Offset(0.68f, 0.64f), Offset(0.74f, 0.70f), Offset(0.68f, 0.78f), Offset(0.62f, 0.70f), Offset(0.68f, 0.64f)
                ),
                // Stroke 3: Window Dividers & Headlight
                listOf(
                    Offset(0.52f, 0.38f), Offset(0.52f, 0.54f), Offset(0.70f, 0.54f)
                )
            )
        ),

        // 3. TEDDY BEAR (Distinct head, circular ears, snout, arms & feet)
        CartoonThingInfo(
            id = "teddy",
            enName = "Teddy Bear",
            bmName = "Beruang Mainan",
            emoji = "🧸",
            imageRes = R.drawable.thing_teddy,
            strokeColor = Color(0xFFD97706),
            playSound = { SoundEngine.playBoing() },
            strokes = listOf(
                // Stroke 0: Head Contour
                listOf(
                    Offset(0.50f, 0.22f), Offset(0.62f, 0.25f), Offset(0.68f, 0.36f), Offset(0.62f, 0.46f),
                    Offset(0.50f, 0.50f), Offset(0.38f, 0.46f), Offset(0.32f, 0.36f), Offset(0.38f, 0.25f), Offset(0.50f, 0.22f)
                ),
                // Stroke 1: Left Ear
                listOf(
                    Offset(0.36f, 0.26f), Offset(0.26f, 0.20f), Offset(0.22f, 0.12f), Offset(0.32f, 0.14f), Offset(0.42f, 0.22f)
                ),
                // Stroke 2: Right Ear
                listOf(
                    Offset(0.58f, 0.22f), Offset(0.68f, 0.14f), Offset(0.78f, 0.12f), Offset(0.74f, 0.20f), Offset(0.64f, 0.26f)
                ),
                // Stroke 3: Snout & Nose
                listOf(
                    Offset(0.50f, 0.36f), Offset(0.56f, 0.40f), Offset(0.50f, 0.44f), Offset(0.44f, 0.40f), Offset(0.50f, 0.36f)
                ),
                // Stroke 4: Body, Arms & Feet
                listOf(
                    Offset(0.38f, 0.50f), Offset(0.22f, 0.56f), Offset(0.24f, 0.66f), Offset(0.34f, 0.68f),
                    Offset(0.34f, 0.86f), Offset(0.44f, 0.86f), Offset(0.50f, 0.78f), Offset(0.56f, 0.86f),
                    Offset(0.66f, 0.86f), Offset(0.66f, 0.68f), Offset(0.76f, 0.66f), Offset(0.78f, 0.56f),
                    Offset(0.62f, 0.50f)
                )
            )
        ),

        // 4. BALLOON (Oval balloon, knot, squiggly string)
        CartoonThingInfo(
            id = "balloon",
            enName = "Floating Balloon",
            bmName = "Belon Terapung",
            emoji = "🎈",
            imageRes = R.drawable.thing_balloon,
            strokeColor = Color(0xFFEF4444),
            playSound = { SoundEngine.playPop() },
            strokes = listOf(
                // Stroke 0: Oval Balloon Body
                listOf(
                    Offset(0.50f, 0.15f), Offset(0.68f, 0.22f), Offset(0.76f, 0.38f), Offset(0.72f, 0.54f),
                    Offset(0.58f, 0.68f), Offset(0.50f, 0.72f), Offset(0.42f, 0.68f), Offset(0.28f, 0.54f),
                    Offset(0.24f, 0.38f), Offset(0.32f, 0.22f), Offset(0.50f, 0.15f)
                ),
                // Stroke 1: Balloon Knot
                listOf(
                    Offset(0.50f, 0.72f), Offset(0.45f, 0.76f), Offset(0.55f, 0.76f), Offset(0.50f, 0.72f)
                ),
                // Stroke 2: Squiggly String
                listOf(
                    Offset(0.50f, 0.76f), Offset(0.44f, 0.81f), Offset(0.56f, 0.86f), Offset(0.46f, 0.91f), Offset(0.52f, 0.95f)
                )
            )
        ),

        // 5. BIRTHDAY CAKE (Tiered cake silhouette, candles, flame)
        CartoonThingInfo(
            id = "cake",
            enName = "Birthday Cake",
            bmName = "Kek Hari Jadi",
            emoji = "🎂",
            imageRes = R.drawable.thing_cake,
            strokeColor = Color(0xFFEC4899),
            playSound = { SoundEngine.playCelebrationFanfare() },
            strokes = listOf(
                // Stroke 0: Bottom Cake Tier
                listOf(
                    Offset(0.18f, 0.84f), Offset(0.82f, 0.84f), Offset(0.80f, 0.64f), Offset(0.20f, 0.64f), Offset(0.18f, 0.84f)
                ),
                // Stroke 1: Top Cake Tier
                listOf(
                    Offset(0.28f, 0.64f), Offset(0.28f, 0.44f), Offset(0.72f, 0.44f), Offset(0.72f, 0.64f)
                ),
                // Stroke 2: Birthday Candle
                listOf(
                    Offset(0.48f, 0.44f), Offset(0.48f, 0.28f), Offset(0.52f, 0.28f), Offset(0.52f, 0.44f)
                ),
                // Stroke 3: Candle Flame
                listOf(
                    Offset(0.50f, 0.28f), Offset(0.44f, 0.22f), Offset(0.50f, 0.15f), Offset(0.56f, 0.22f), Offset(0.50f, 0.28f)
                )
            )
        )
    )
}
