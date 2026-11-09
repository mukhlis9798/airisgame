package com.pocketdev.airisgame.ui.screens

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

data class LetterStrokeData(
    val letter: Char,
    val enWord: String,
    val bmWord: String,
    val emoji: String,
    val capitalStrokes: List<List<Offset>>,
    val smallStrokes: List<List<Offset>>
)

@Composable
fun AbcDotsScreen(onBack: () -> Unit) {
    val confettiState = rememberConfettiState()
    val haptic = LocalHapticFeedback.current
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val isTablet = screenWidth >= 600

    var isCapital by remember { mutableStateOf(true) }
    var selectedLetterIndex by remember { mutableStateOf(0) }
    var currentStrokeIndex by remember { mutableStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }

    // User's currently active live trace points on screen
    val userLiveTrace = remember { mutableStateListOf<Offset>() }
    // Which normalized points in the active stroke have been covered by the toddler's finger
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

    val letters = remember { getEducationalLetters() }
    val currentItem = letters[selectedLetterIndex.coerceIn(0, letters.lastIndex)]
    val activeStrokes = if (isCapital) currentItem.capitalStrokes else currentItem.smallStrokes

    // Reset tracing state when letter or case changes
    LaunchedEffect(selectedLetterIndex, isCapital) {
        currentStrokeIndex = 0
        isCompleted = false
        userLiveTrace.clear()
        coveredPoints.clear()

        val charShow = if (isCapital) currentItem.letter.uppercaseChar() else currentItem.letter.lowercaseChar()
        val word = if (LanguageManager.isMalay()) currentItem.bmWord else currentItem.enWord
        SpeechEngine.speak("$charShow. $word")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with non-overlapping Row (Back, Title, Lang, Sound, Lock)
            ToddlerAppHeader(
                title = LanguageManager.abcDotsTitle(),
                subtitle = LanguageManager.abcDotsSubtitle(),
                onBack = onBack
            )

            // Case Toggle Pills (Capital / Small)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LetterCasePill(
                    text = LanguageManager.capitalLetters(),
                    isActive = isCapital,
                    activeColor = SkyVibrant,
                    onClick = {
                        if (!isCapital) {
                            SoundEngine.playPop()
                            isCapital = true
                        }
                    }
                )

                Spacer(modifier = Modifier.width(12.dp))

                LetterCasePill(
                    text = LanguageManager.smallLetters(),
                    isActive = !isCapital,
                    activeColor = CandyCoral,
                    onClick = {
                        if (isCapital) {
                            SoundEngine.playPop()
                            isCapital = false
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Scrollable Alphabet Selector Bar (A to Z)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(letters) { index, item ->
                    val isSelected = index == selectedLetterIndex
                    val charShow = if (isCapital) item.letter.uppercaseChar() else item.letter.lowercaseChar()

                    Box(
                        modifier = Modifier
                            .size(if (isTablet) 46.dp else 40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) (if (isCapital) SkyVibrant else CandyCoral)
                                else Color.White.copy(alpha = 0.92f)
                            )
                            .border(
                                1.5.dp,
                                if (isSelected) Color.White else Color(0xFFE2E8F0),
                                CircleShape
                            )
                            .clickable {
                                if (index != selectedLetterIndex) {
                                    SoundEngine.playPop()
                                    selectedLetterIndex = index
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = charShow.toString(),
                            fontSize = if (isTablet) 20.sp else 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) Color.White else TextDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CENTER: Tracing Card with Authentic Typography Stencil & Freeform Tracing Canvas
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
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .pointerInput(selectedLetterIndex, isCapital, currentStrokeIndex, isCompleted) {
                                if (isCompleted) return@pointerInput

                                awaitPointerEventScope {
                                    while (true) {
                                        val down = awaitFirstDown(requireUnconsumed = false)
                                        val activePointerId = down.id
                                        val canvasW = size.width
                                        val canvasH = size.height

                                        if (currentStrokeIndex >= activeStrokes.size) break
                                        val targetStroke = activeStrokes[currentStrokeIndex]
                                        val hitTolerancePx = 38.dp.toPx()

                                        fun processTouch(pos: Offset) {
                                            userLiveTrace.add(pos)
                                            // Check distance to all points along the target educational stroke
                                            targetStroke.forEachIndexed { ptIdx, normPt ->
                                                val actualPt = Offset(normPt.x * canvasW, normPt.y * canvasH)
                                                val dist = hypot(pos.x - actualPt.x, pos.y - actualPt.y)
                                                if (dist < hitTolerancePx && !coveredPoints.contains(ptIdx)) {
                                                    coveredPoints.add(ptIdx)
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                }
                                            }

                                            // Stroke completed when >= 70% of points covered, or start and end both touched
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
                                                    SoundEngine.playCelebrationFanfare()
                                                    confettiState.spawn(300f, 300f, 45)
                                                    val letterChar = if (isCapital) currentItem.letter.uppercaseChar() else currentItem.letter.lowercaseChar()
                                                    val word = if (LanguageManager.isMalay()) currentItem.bmWord else currentItem.enWord
                                                    SpeechEngine.speak(
                                                        if (LanguageManager.isMalay())
                                                            "Hebat! Huruf $letterChar! $letterChar untuk $word! ${currentItem.emoji}"
                                                        else
                                                            "Awesome! Letter $letterChar! $letterChar is for $word! ${currentItem.emoji}"
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
                        val letterChar = if (isCapital) currentItem.letter.uppercaseChar() else currentItem.letter.lowercaseChar()
                        val letterStr = letterChar.toString()

                        // 1. FAINT AUTHENTIC LETTER STENCIL BACKGROUND (alpha = 0.12f)
                        drawIntoCanvas { canvas ->
                            val paint = Paint().apply {
                                color = android.graphics.Color.BLACK
                                alpha = (255 * 0.12f).toInt()
                                textSize = size.minDimension * 0.82f
                                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                                textAlign = Paint.Align.CENTER
                                isAntiAlias = true
                            }
                            val bounds = Rect()
                            paint.getTextBounds(letterStr, 0, letterStr.length, bounds)
                            val textY = (h / 2f) + (bounds.height() / 2f) - bounds.bottom
                            canvas.nativeCanvas.drawText(letterStr, w / 2f, textY, paint)
                        }

                        // 2. UNTRACED STROKES AS PROMINENT DASHED OUTLINES (dash 20f, 15f)
                        activeStrokes.forEachIndexed { strokeIdx, strokePts ->
                            if (strokeIdx >= currentStrokeIndex && strokePts.size > 1) {
                                val dashedPath = Path().apply {
                                    val start = Offset(strokePts[0].x * w, strokePts[0].y * h)
                                    moveTo(start.x, start.y)
                                    for (i in 1 until strokePts.size) {
                                        val p = Offset(strokePts[i].x * w, strokePts[i].y * h)
                                        lineTo(p.x, p.y)
                                    }
                                }
                                drawPath(
                                    path = dashedPath,
                                    color = Color(0xFFCBD5E1),
                                    style = Stroke(
                                        width = 12.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f),
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }

                        // 3. COMPLETED STROKES LOCKED INTO SOLID VIBRANT COLORED LINES
                        activeStrokes.forEachIndexed { strokeIdx, strokePts ->
                            if (strokeIdx < currentStrokeIndex && strokePts.size > 1) {
                                val solidPath = Path().apply {
                                    val start = Offset(strokePts[0].x * w, strokePts[0].y * h)
                                    moveTo(start.x, start.y)
                                    for (i in 1 until strokePts.size) {
                                        val p = Offset(strokePts[i].x * w, strokePts[i].y * h)
                                        lineTo(p.x, p.y)
                                    }
                                }
                                drawPath(
                                    path = solidPath,
                                    color = if (isCapital) SkyVibrant else CandyCoral,
                                    style = Stroke(
                                        width = 24.dp.toPx(),
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }

                        // 4. ACTIVE LIVE TRACE DRAWN AS THICK SOLID STROKE IN REAL-TIME
                        if (userLiveTrace.size > 1 && currentStrokeIndex < activeStrokes.size) {
                            val livePath = Path().apply {
                                moveTo(userLiveTrace[0].x, userLiveTrace[0].y)
                                for (i in 1 until userLiveTrace.size) {
                                    lineTo(userLiveTrace[i].x, userLiveTrace[i].y)
                                }
                            }
                            drawPath(
                                path = livePath,
                                color = (if (isCapital) SkyVibrant else CandyCoral).copy(alpha = 0.85f),
                                style = Stroke(
                                    width = 24.dp.toPx(),
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
                                // Glow
                                drawCircle(
                                    color = SunnyYellow.copy(alpha = 0.55f),
                                    radius = 24.dp.toPx() * pulseScale,
                                    center = startPt
                                )
                                // Solid center
                                drawCircle(
                                    color = SunnyYellow,
                                    radius = 16.dp.toPx(),
                                    center = startPt
                                )
                                // Number label inside badge
                                drawIntoCanvas { canvas ->
                                    val numPaint = Paint().apply {
                                        color = android.graphics.Color.WHITE
                                        textSize = 14.sp.toPx()
                                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                                        textAlign = Paint.Align.CENTER
                                        isAntiAlias = true
                                    }
                                    val numBounds = Rect()
                                    val numStr = (currentStrokeIndex + 1).toString()
                                    numPaint.getTextBounds(numStr, 0, numStr.length, numBounds)
                                    val numY = startPt.y + (numBounds.height() / 2f) - numBounds.bottom
                                    canvas.nativeCanvas.drawText(numStr, startPt.x, numY, numPaint)
                                }
                            }
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
                                        val charShow = if (isCapital) currentItem.letter.uppercaseChar() else currentItem.letter.lowercaseChar()
                                        val word = if (LanguageManager.isMalay()) currentItem.bmWord else currentItem.enWord
                                        Text(
                                            text = "$charShow untuk $word!",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TextDark
                                        )
                                        Text(
                                            text = if (LanguageManager.isMalay()) "Tahniah! Hebat sekali!" else "Great job! Keep tracing!",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextDark.copy(alpha = 0.65f)
                                        )
                                    }
                                }

                                ToddlerButton(
                                    text = if (LanguageManager.isMalay()) "Seterusnya" else "Next",
                                    icon = "➡️",
                                    buttonColor = SkyVibrant,
                                    shadowColor = CoralShadow,
                                    onClick = {
                                        SoundEngine.playBoing()
                                        selectedLetterIndex = (selectedLetterIndex + 1) % letters.size
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Celebration Confetti Overlay
        ConfettiOverlay(state = confettiState)
    }
}

@Composable
private fun LetterCasePill(
    text: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(19.dp))
            .background(if (isActive) activeColor else Color.White.copy(alpha = 0.92f))
            .border(
                1.5.dp,
                if (isActive) Color.White else Color(0xFFE2E8F0),
                RoundedCornerShape(19.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isActive) Color.White else TextDark
        )
    }
}

/**
 * Generates educational handwriting stroke paths for standard sans-serif letterforms.
 */
private fun getEducationalLetters(): List<LetterStrokeData> {
    return listOf(
        // A
        LetterStrokeData('A', "Apple", "Ayam", "🍎",
            capitalStrokes = listOf(
                listOf(Offset(0.25f, 0.82f), Offset(0.33f, 0.60f), Offset(0.42f, 0.38f), Offset(0.50f, 0.16f)),
                listOf(Offset(0.50f, 0.16f), Offset(0.58f, 0.38f), Offset(0.67f, 0.60f), Offset(0.75f, 0.82f)),
                listOf(Offset(0.35f, 0.54f), Offset(0.50f, 0.54f), Offset(0.65f, 0.54f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.66f, 0.46f), Offset(0.50f, 0.38f), Offset(0.34f, 0.52f), Offset(0.34f, 0.70f), Offset(0.50f, 0.82f), Offset(0.66f, 0.75f)),
                listOf(Offset(0.66f, 0.38f), Offset(0.66f, 0.60f), Offset(0.66f, 0.82f))
            )
        ),
        // B
        LetterStrokeData('B', "Ball", "Bola", "⚽",
            capitalStrokes = listOf(
                listOf(Offset(0.28f, 0.16f), Offset(0.28f, 0.49f), Offset(0.28f, 0.82f)),
                listOf(Offset(0.28f, 0.16f), Offset(0.65f, 0.18f), Offset(0.72f, 0.33f), Offset(0.65f, 0.49f), Offset(0.28f, 0.49f)),
                listOf(Offset(0.28f, 0.49f), Offset(0.68f, 0.51f), Offset(0.76f, 0.66f), Offset(0.68f, 0.82f), Offset(0.28f, 0.82f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.30f, 0.16f), Offset(0.30f, 0.49f), Offset(0.30f, 0.82f)),
                listOf(Offset(0.30f, 0.54f), Offset(0.50f, 0.46f), Offset(0.70f, 0.58f), Offset(0.70f, 0.74f), Offset(0.50f, 0.82f), Offset(0.30f, 0.80f))
            )
        ),
        // C
        LetterStrokeData('C', "Cat", "Cawan", "🐱",
            capitalStrokes = listOf(
                listOf(Offset(0.74f, 0.28f), Offset(0.50f, 0.16f), Offset(0.26f, 0.35f), Offset(0.24f, 0.50f), Offset(0.26f, 0.65f), Offset(0.50f, 0.82f), Offset(0.74f, 0.72f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.70f, 0.46f), Offset(0.50f, 0.38f), Offset(0.32f, 0.52f), Offset(0.32f, 0.68f), Offset(0.50f, 0.82f), Offset(0.70f, 0.74f))
            )
        ),
        // D
        LetterStrokeData('D', "Duck", "Daun", "🦆",
            capitalStrokes = listOf(
                listOf(Offset(0.28f, 0.16f), Offset(0.28f, 0.49f), Offset(0.28f, 0.82f)),
                listOf(Offset(0.28f, 0.16f), Offset(0.65f, 0.20f), Offset(0.76f, 0.49f), Offset(0.65f, 0.78f), Offset(0.28f, 0.82f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.68f, 0.52f), Offset(0.50f, 0.46f), Offset(0.32f, 0.58f), Offset(0.32f, 0.74f), Offset(0.50f, 0.82f), Offset(0.68f, 0.78f)),
                listOf(Offset(0.68f, 0.16f), Offset(0.68f, 0.49f), Offset(0.68f, 0.82f))
            )
        ),
        // E
        LetterStrokeData('E', "Elephant", "Epal", "🐘",
            capitalStrokes = listOf(
                listOf(Offset(0.30f, 0.16f), Offset(0.30f, 0.49f), Offset(0.30f, 0.82f)),
                listOf(Offset(0.30f, 0.16f), Offset(0.50f, 0.16f), Offset(0.72f, 0.16f)),
                listOf(Offset(0.30f, 0.49f), Offset(0.48f, 0.49f), Offset(0.65f, 0.49f)),
                listOf(Offset(0.30f, 0.82f), Offset(0.50f, 0.82f), Offset(0.72f, 0.82f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.32f, 0.60f), Offset(0.70f, 0.60f)),
                listOf(Offset(0.70f, 0.60f), Offset(0.68f, 0.44f), Offset(0.50f, 0.38f), Offset(0.32f, 0.52f), Offset(0.32f, 0.68f), Offset(0.50f, 0.82f), Offset(0.70f, 0.74f))
            )
        ),
        // F
        LetterStrokeData('F', "Fish", "Feri", "🐟",
            capitalStrokes = listOf(
                listOf(Offset(0.30f, 0.16f), Offset(0.30f, 0.49f), Offset(0.30f, 0.82f)),
                listOf(Offset(0.30f, 0.16f), Offset(0.50f, 0.16f), Offset(0.72f, 0.16f)),
                listOf(Offset(0.30f, 0.49f), Offset(0.48f, 0.49f), Offset(0.65f, 0.49f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.65f, 0.22f), Offset(0.50f, 0.16f), Offset(0.42f, 0.30f), Offset(0.42f, 0.82f)),
                listOf(Offset(0.30f, 0.46f), Offset(0.58f, 0.46f))
            )
        ),
        // G
        LetterStrokeData('G', "Giraffe", "Gajah", "🦒",
            capitalStrokes = listOf(
                listOf(Offset(0.74f, 0.28f), Offset(0.50f, 0.16f), Offset(0.26f, 0.35f), Offset(0.24f, 0.50f), Offset(0.26f, 0.65f), Offset(0.50f, 0.82f), Offset(0.74f, 0.70f), Offset(0.74f, 0.54f)),
                listOf(Offset(0.74f, 0.54f), Offset(0.52f, 0.54f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.68f, 0.46f), Offset(0.50f, 0.38f), Offset(0.34f, 0.50f), Offset(0.34f, 0.68f), Offset(0.50f, 0.76f), Offset(0.68f, 0.72f)),
                listOf(Offset(0.68f, 0.38f), Offset(0.68f, 0.74f), Offset(0.65f, 0.90f), Offset(0.48f, 0.92f), Offset(0.34f, 0.85f))
            )
        ),
        // H
        LetterStrokeData('H', "Horse", "Harimau", "🐴",
            capitalStrokes = listOf(
                listOf(Offset(0.28f, 0.16f), Offset(0.28f, 0.49f), Offset(0.28f, 0.82f)),
                listOf(Offset(0.72f, 0.16f), Offset(0.72f, 0.49f), Offset(0.72f, 0.82f)),
                listOf(Offset(0.28f, 0.49f), Offset(0.50f, 0.49f), Offset(0.72f, 0.49f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.32f, 0.16f), Offset(0.32f, 0.49f), Offset(0.32f, 0.82f)),
                listOf(Offset(0.32f, 0.52f), Offset(0.50f, 0.44f), Offset(0.68f, 0.56f), Offset(0.68f, 0.82f))
            )
        ),
        // I
        LetterStrokeData('I', "Ice Cream", "Ikan", "🍦",
            capitalStrokes = listOf(
                listOf(Offset(0.50f, 0.16f), Offset(0.50f, 0.49f), Offset(0.50f, 0.82f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.50f, 0.44f), Offset(0.50f, 0.63f), Offset(0.50f, 0.82f)),
                listOf(Offset(0.50f, 0.24f), Offset(0.50f, 0.28f))
            )
        ),
        // J
        LetterStrokeData('J', "Juice", "Jagung", "🧃",
            capitalStrokes = listOf(
                listOf(Offset(0.66f, 0.16f), Offset(0.66f, 0.60f), Offset(0.56f, 0.78f), Offset(0.40f, 0.82f), Offset(0.28f, 0.72f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.58f, 0.42f), Offset(0.58f, 0.76f), Offset(0.50f, 0.90f), Offset(0.36f, 0.88f), Offset(0.28f, 0.80f)),
                listOf(Offset(0.58f, 0.24f), Offset(0.58f, 0.28f))
            )
        ),
        // K
        LetterStrokeData('K', "Kite", "Kucing", "🪁",
            capitalStrokes = listOf(
                listOf(Offset(0.30f, 0.16f), Offset(0.30f, 0.49f), Offset(0.30f, 0.82f)),
                listOf(Offset(0.70f, 0.20f), Offset(0.50f, 0.35f), Offset(0.32f, 0.49f)),
                listOf(Offset(0.32f, 0.49f), Offset(0.52f, 0.66f), Offset(0.72f, 0.82f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.32f, 0.16f), Offset(0.32f, 0.49f), Offset(0.32f, 0.82f)),
                listOf(Offset(0.66f, 0.44f), Offset(0.34f, 0.63f)),
                listOf(Offset(0.34f, 0.63f), Offset(0.68f, 0.82f))
            )
        ),
        // L
        LetterStrokeData('L', "Lion", "Lembu", "🦁",
            capitalStrokes = listOf(
                listOf(Offset(0.32f, 0.16f), Offset(0.32f, 0.49f), Offset(0.32f, 0.82f)),
                listOf(Offset(0.32f, 0.82f), Offset(0.52f, 0.82f), Offset(0.72f, 0.82f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.50f, 0.16f), Offset(0.50f, 0.49f), Offset(0.50f, 0.82f))
            )
        ),
        // M
        LetterStrokeData('M', "Monkey", "Monyet", "🐒",
            capitalStrokes = listOf(
                listOf(Offset(0.22f, 0.82f), Offset(0.22f, 0.49f), Offset(0.22f, 0.16f)),
                listOf(Offset(0.22f, 0.16f), Offset(0.36f, 0.40f), Offset(0.50f, 0.60f)),
                listOf(Offset(0.50f, 0.60f), Offset(0.64f, 0.40f), Offset(0.78f, 0.16f)),
                listOf(Offset(0.78f, 0.16f), Offset(0.78f, 0.49f), Offset(0.78f, 0.82f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.24f, 0.42f), Offset(0.24f, 0.62f), Offset(0.24f, 0.82f)),
                listOf(Offset(0.24f, 0.50f), Offset(0.38f, 0.42f), Offset(0.50f, 0.54f), Offset(0.50f, 0.82f)),
                listOf(Offset(0.50f, 0.50f), Offset(0.64f, 0.42f), Offset(0.76f, 0.54f), Offset(0.76f, 0.82f))
            )
        ),
        // N
        LetterStrokeData('N', "Nest", "Nanas", "🪺",
            capitalStrokes = listOf(
                listOf(Offset(0.26f, 0.82f), Offset(0.26f, 0.49f), Offset(0.26f, 0.16f)),
                listOf(Offset(0.26f, 0.16f), Offset(0.50f, 0.49f), Offset(0.74f, 0.82f)),
                listOf(Offset(0.74f, 0.82f), Offset(0.74f, 0.49f), Offset(0.74f, 0.16f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.30f, 0.42f), Offset(0.30f, 0.62f), Offset(0.30f, 0.82f)),
                listOf(Offset(0.30f, 0.52f), Offset(0.50f, 0.42f), Offset(0.70f, 0.56f), Offset(0.70f, 0.82f))
            )
        ),
        // O
        LetterStrokeData('O', "Owl", "Oren", "🦉",
            capitalStrokes = listOf(
                listOf(Offset(0.50f, 0.16f), Offset(0.76f, 0.32f), Offset(0.78f, 0.50f), Offset(0.76f, 0.68f), Offset(0.50f, 0.84f), Offset(0.24f, 0.68f), Offset(0.22f, 0.50f), Offset(0.24f, 0.32f), Offset(0.50f, 0.16f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.50f, 0.38f), Offset(0.70f, 0.48f), Offset(0.72f, 0.60f), Offset(0.70f, 0.72f), Offset(0.50f, 0.82f), Offset(0.30f, 0.72f), Offset(0.28f, 0.60f), Offset(0.30f, 0.48f), Offset(0.50f, 0.38f))
            )
        ),
        // P
        LetterStrokeData('P', "Penguin", "Pisang", "🐧",
            capitalStrokes = listOf(
                listOf(Offset(0.28f, 0.16f), Offset(0.28f, 0.49f), Offset(0.28f, 0.82f)),
                listOf(Offset(0.28f, 0.16f), Offset(0.68f, 0.18f), Offset(0.74f, 0.34f), Offset(0.68f, 0.50f), Offset(0.28f, 0.50f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.32f, 0.40f), Offset(0.32f, 0.68f), Offset(0.32f, 0.94f)),
                listOf(Offset(0.32f, 0.48f), Offset(0.52f, 0.40f), Offset(0.70f, 0.50f), Offset(0.70f, 0.66f), Offset(0.52f, 0.76f), Offset(0.32f, 0.74f))
            )
        ),
        // Q
        LetterStrokeData('Q', "Queen", "Quran", "👑",
            capitalStrokes = listOf(
                listOf(Offset(0.50f, 0.16f), Offset(0.76f, 0.32f), Offset(0.78f, 0.50f), Offset(0.76f, 0.68f), Offset(0.50f, 0.84f), Offset(0.24f, 0.68f), Offset(0.22f, 0.50f), Offset(0.24f, 0.32f), Offset(0.50f, 0.16f)),
                listOf(Offset(0.58f, 0.68f), Offset(0.68f, 0.78f), Offset(0.78f, 0.86f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.68f, 0.46f), Offset(0.50f, 0.38f), Offset(0.32f, 0.50f), Offset(0.32f, 0.68f), Offset(0.50f, 0.76f), Offset(0.68f, 0.72f)),
                listOf(Offset(0.68f, 0.38f), Offset(0.68f, 0.68f), Offset(0.68f, 0.94f))
            )
        ),
        // R
        LetterStrokeData('R', "Rabbit", "Roti", "🐰",
            capitalStrokes = listOf(
                listOf(Offset(0.28f, 0.16f), Offset(0.28f, 0.49f), Offset(0.28f, 0.82f)),
                listOf(Offset(0.28f, 0.16f), Offset(0.68f, 0.18f), Offset(0.74f, 0.34f), Offset(0.68f, 0.50f), Offset(0.28f, 0.50f)),
                listOf(Offset(0.48f, 0.50f), Offset(0.62f, 0.66f), Offset(0.74f, 0.82f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.36f, 0.40f), Offset(0.36f, 0.61f), Offset(0.36f, 0.82f)),
                listOf(Offset(0.36f, 0.52f), Offset(0.50f, 0.40f), Offset(0.66f, 0.44f))
            )
        ),
        // S
        LetterStrokeData('S', "Star", "Susu", "⭐",
            capitalStrokes = listOf(
                listOf(Offset(0.72f, 0.28f), Offset(0.50f, 0.16f), Offset(0.30f, 0.32f), Offset(0.50f, 0.49f), Offset(0.72f, 0.66f), Offset(0.50f, 0.84f), Offset(0.28f, 0.72f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.68f, 0.48f), Offset(0.50f, 0.40f), Offset(0.34f, 0.50f), Offset(0.50f, 0.61f), Offset(0.68f, 0.72f), Offset(0.50f, 0.82f), Offset(0.32f, 0.74f))
            )
        ),
        // T
        LetterStrokeData('T', "Tiger", "Topi", "🐯",
            capitalStrokes = listOf(
                listOf(Offset(0.24f, 0.16f), Offset(0.50f, 0.16f), Offset(0.76f, 0.16f)),
                listOf(Offset(0.50f, 0.16f), Offset(0.50f, 0.49f), Offset(0.50f, 0.82f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.48f, 0.22f), Offset(0.48f, 0.68f), Offset(0.56f, 0.82f), Offset(0.66f, 0.80f)),
                listOf(Offset(0.32f, 0.42f), Offset(0.64f, 0.42f))
            )
        ),
        // U
        LetterStrokeData('U', "Umbrella", "Ubat", "☂️",
            capitalStrokes = listOf(
                listOf(Offset(0.28f, 0.16f), Offset(0.28f, 0.56f), Offset(0.38f, 0.78f), Offset(0.50f, 0.82f), Offset(0.62f, 0.78f), Offset(0.72f, 0.56f), Offset(0.72f, 0.16f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.32f, 0.42f), Offset(0.32f, 0.66f), Offset(0.44f, 0.80f), Offset(0.56f, 0.80f), Offset(0.68f, 0.66f), Offset(0.68f, 0.42f)),
                listOf(Offset(0.68f, 0.42f), Offset(0.68f, 0.62f), Offset(0.68f, 0.82f))
            )
        ),
        // V
        LetterStrokeData('V', "Van", "Van", "🚐",
            capitalStrokes = listOf(
                listOf(Offset(0.26f, 0.16f), Offset(0.38f, 0.49f), Offset(0.50f, 0.82f)),
                listOf(Offset(0.50f, 0.82f), Offset(0.62f, 0.49f), Offset(0.74f, 0.16f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.32f, 0.42f), Offset(0.50f, 0.82f)),
                listOf(Offset(0.50f, 0.82f), Offset(0.68f, 0.42f))
            )
        ),
        // W
        LetterStrokeData('W', "Watermelon", "Wau", "🍉",
            capitalStrokes = listOf(
                listOf(Offset(0.20f, 0.16f), Offset(0.28f, 0.49f), Offset(0.36f, 0.82f)),
                listOf(Offset(0.36f, 0.82f), Offset(0.43f, 0.54f), Offset(0.50f, 0.32f)),
                listOf(Offset(0.50f, 0.32f), Offset(0.57f, 0.54f), Offset(0.64f, 0.82f)),
                listOf(Offset(0.64f, 0.82f), Offset(0.72f, 0.49f), Offset(0.80f, 0.16f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.22f, 0.44f), Offset(0.36f, 0.82f)),
                listOf(Offset(0.36f, 0.82f), Offset(0.50f, 0.52f)),
                listOf(Offset(0.50f, 0.52f), Offset(0.64f, 0.82f)),
                listOf(Offset(0.64f, 0.82f), Offset(0.78f, 0.44f))
            )
        ),
        // X
        LetterStrokeData('X', "Xylophone", "Xilofon", "🎹",
            capitalStrokes = listOf(
                listOf(Offset(0.26f, 0.16f), Offset(0.50f, 0.49f), Offset(0.74f, 0.82f)),
                listOf(Offset(0.74f, 0.16f), Offset(0.50f, 0.49f), Offset(0.26f, 0.82f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.32f, 0.42f), Offset(0.68f, 0.82f)),
                listOf(Offset(0.68f, 0.42f), Offset(0.32f, 0.82f))
            )
        ),
        // Y
        LetterStrokeData('Y', "Yo-Yo", "Yis", "🪀",
            capitalStrokes = listOf(
                listOf(Offset(0.26f, 0.16f), Offset(0.50f, 0.49f)),
                listOf(Offset(0.74f, 0.16f), Offset(0.50f, 0.49f)),
                listOf(Offset(0.50f, 0.49f), Offset(0.50f, 0.82f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.32f, 0.42f), Offset(0.50f, 0.66f)),
                listOf(Offset(0.68f, 0.42f), Offset(0.50f, 0.66f), Offset(0.38f, 0.88f), Offset(0.28f, 0.86f))
            )
        ),
        // Z
        LetterStrokeData('Z', "Zebra", "Zirafah", "🦓",
            capitalStrokes = listOf(
                listOf(Offset(0.28f, 0.18f), Offset(0.50f, 0.18f), Offset(0.72f, 0.18f)),
                listOf(Offset(0.72f, 0.18f), Offset(0.50f, 0.50f), Offset(0.28f, 0.82f)),
                listOf(Offset(0.28f, 0.82f), Offset(0.50f, 0.82f), Offset(0.72f, 0.82f))
            ),
            smallStrokes = listOf(
                listOf(Offset(0.32f, 0.44f), Offset(0.68f, 0.44f)),
                listOf(Offset(0.68f, 0.44f), Offset(0.32f, 0.82f)),
                listOf(Offset(0.32f, 0.82f), Offset(0.68f, 0.82f))
            )
        )
    )
}
