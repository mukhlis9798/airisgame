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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import com.pocketdev.airisgame.ui.theme.MagicLavender
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.SkyShadow
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark
import kotlin.math.hypot

data class DotLetterInfo(
    val letter: Char,
    val enWord: String,
    val bmWord: String,
    val emoji: String,
    val capitalDots: List<Offset>,
    val smallDots: List<Offset>,
    val isCapitalCurved: Boolean = false,
    val isSmallCurved: Boolean = false,
    val capitalStraightIndices: Set<Int> = emptySet(),
    val smallStraightIndices: Set<Int> = emptySet()
)

@Composable
fun AbcDotsScreen(onBack: () -> Unit) {
    val confettiState = rememberConfettiState()
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val isTablet = screenWidth >= 600

    var isCapital by remember { mutableStateOf(true) }
    var selectedLetterIndex by remember { mutableStateOf(0) }
    var completedDotsCount by remember { mutableStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.32f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val letters = remember {
        listOf(
            // A: Capital is straight, Small is curved around belly
            DotLetterInfo('A', "Apple", "Ayam", "🍎",
                capitalDots = listOf(Offset(0.18f, 0.85f), Offset(0.50f, 0.15f), Offset(0.82f, 0.85f), Offset(0.66f, 0.58f), Offset(0.34f, 0.58f)),
                smallDots = listOf(Offset(0.68f, 0.40f), Offset(0.48f, 0.36f), Offset(0.28f, 0.58f), Offset(0.48f, 0.80f), Offset(0.68f, 0.72f), Offset(0.68f, 0.85f)),
                isCapitalCurved = false,
                isSmallCurved = true,
                smallStraightIndices = setOf(4)
            ),
            // B: Capital has straight spine + curved bumps; Small has straight stem + curved belly
            DotLetterInfo('B', "Ball", "Bola", "⚽",
                capitalDots = listOf(Offset(0.25f, 0.85f), Offset(0.25f, 0.15f), Offset(0.68f, 0.28f), Offset(0.35f, 0.50f), Offset(0.72f, 0.68f), Offset(0.25f, 0.85f)),
                smallDots = listOf(Offset(0.30f, 0.15f), Offset(0.30f, 0.85f), Offset(0.52f, 0.85f), Offset(0.72f, 0.68f), Offset(0.52f, 0.50f), Offset(0.30f, 0.55f)),
                isCapitalCurved = true,
                isSmallCurved = true,
                capitalStraightIndices = setOf(0),
                smallStraightIndices = setOf(0)
            ),
            // C: Curved open loop
            DotLetterInfo('C', "Cat", "Cawan", "🐱",
                capitalDots = listOf(Offset(0.76f, 0.26f), Offset(0.48f, 0.16f), Offset(0.25f, 0.50f), Offset(0.48f, 0.84f), Offset(0.76f, 0.74f)),
                smallDots = listOf(Offset(0.72f, 0.44f), Offset(0.48f, 0.38f), Offset(0.28f, 0.60f), Offset(0.48f, 0.82f), Offset(0.72f, 0.76f)),
                isCapitalCurved = true,
                isSmallCurved = true
            ),
            // D: Capital has straight spine + curved belly; Small has curved belly + straight stem
            DotLetterInfo('D', "Duck", "Daun", "🦆",
                capitalDots = listOf(Offset(0.25f, 0.85f), Offset(0.25f, 0.15f), Offset(0.65f, 0.28f), Offset(0.75f, 0.50f), Offset(0.65f, 0.72f), Offset(0.25f, 0.85f)),
                smallDots = listOf(Offset(0.70f, 0.55f), Offset(0.50f, 0.50f), Offset(0.28f, 0.68f), Offset(0.50f, 0.85f), Offset(0.70f, 0.85f), Offset(0.70f, 0.15f)),
                isCapitalCurved = true,
                isSmallCurved = true,
                capitalStraightIndices = setOf(0),
                smallStraightIndices = setOf(4)
            ),
            // E: Capital is straight; Small has straight bar + curved loop
            DotLetterInfo('E', "Elephant", "Epal", "🐘",
                capitalDots = listOf(Offset(0.75f, 0.18f), Offset(0.28f, 0.18f), Offset(0.28f, 0.50f), Offset(0.65f, 0.50f), Offset(0.28f, 0.50f), Offset(0.28f, 0.82f), Offset(0.75f, 0.82f)),
                smallDots = listOf(Offset(0.30f, 0.60f), Offset(0.70f, 0.60f), Offset(0.65f, 0.40f), Offset(0.45f, 0.38f), Offset(0.28f, 0.60f), Offset(0.48f, 0.82f), Offset(0.70f, 0.78f)),
                isCapitalCurved = false,
                isSmallCurved = true,
                smallStraightIndices = setOf(0)
            ),
            // F: Capital is straight; Small has curved hook + straight stem/bar
            DotLetterInfo('F', "Fish", "Feri", "🐟",
                capitalDots = listOf(Offset(0.28f, 0.85f), Offset(0.28f, 0.48f), Offset(0.65f, 0.48f), Offset(0.28f, 0.48f), Offset(0.28f, 0.18f), Offset(0.78f, 0.18f)),
                smallDots = listOf(Offset(0.65f, 0.20f), Offset(0.45f, 0.18f), Offset(0.45f, 0.85f), Offset(0.25f, 0.45f), Offset(0.68f, 0.45f)),
                isCapitalCurved = false,
                isSmallCurved = true,
                smallStraightIndices = setOf(1, 3)
            ),
            // G: Capital is curved + straight bar; Small is curved loop
            DotLetterInfo('G', "Giraffe", "Gajah", "🦒",
                capitalDots = listOf(Offset(0.78f, 0.28f), Offset(0.50f, 0.15f), Offset(0.25f, 0.50f), Offset(0.48f, 0.85f), Offset(0.78f, 0.80f), Offset(0.78f, 0.52f), Offset(0.55f, 0.52f)),
                smallDots = listOf(Offset(0.68f, 0.42f), Offset(0.38f, 0.40f), Offset(0.28f, 0.60f), Offset(0.48f, 0.78f), Offset(0.68f, 0.60f), Offset(0.68f, 0.90f), Offset(0.42f, 0.95f)),
                isCapitalCurved = true,
                isSmallCurved = true,
                capitalStraightIndices = setOf(5)
            ),
            // H: Capital straight; Small has straight stem + curved arch
            DotLetterInfo('H', "Horse", "Harimau", "🐴",
                capitalDots = listOf(Offset(0.25f, 0.15f), Offset(0.25f, 0.85f), Offset(0.25f, 0.50f), Offset(0.75f, 0.50f), Offset(0.75f, 0.15f), Offset(0.75f, 0.85f)),
                smallDots = listOf(Offset(0.28f, 0.15f), Offset(0.28f, 0.85f), Offset(0.28f, 0.50f), Offset(0.55f, 0.42f), Offset(0.72f, 0.60f), Offset(0.72f, 0.85f)),
                isCapitalCurved = false,
                isSmallCurved = true,
                smallStraightIndices = setOf(0, 1)
            ),
            // I: Straight lines
            DotLetterInfo('I', "Ice Cream", "Ikan", "🍦",
                capitalDots = listOf(Offset(0.30f, 0.18f), Offset(0.70f, 0.18f), Offset(0.50f, 0.18f), Offset(0.50f, 0.82f), Offset(0.30f, 0.82f), Offset(0.70f, 0.82f)),
                smallDots = listOf(Offset(0.50f, 0.22f), Offset(0.50f, 0.42f), Offset(0.50f, 0.85f)),
                isCapitalCurved = false,
                isSmallCurved = false
            ),
            // J: Straight top + curved hook
            DotLetterInfo('J', "Juice", "Jam", "🧃",
                capitalDots = listOf(Offset(0.32f, 0.18f), Offset(0.75f, 0.18f), Offset(0.65f, 0.18f), Offset(0.65f, 0.65f), Offset(0.48f, 0.85f), Offset(0.28f, 0.75f)),
                smallDots = listOf(Offset(0.60f, 0.22f), Offset(0.60f, 0.42f), Offset(0.60f, 0.78f), Offset(0.40f, 0.90f), Offset(0.25f, 0.80f)),
                isCapitalCurved = true,
                isSmallCurved = true,
                capitalStraightIndices = setOf(0, 1)
            ),
            // K: Straight lines
            DotLetterInfo('K', "Kite", "Kucing", "🪁",
                capitalDots = listOf(Offset(0.28f, 0.15f), Offset(0.28f, 0.85f), Offset(0.75f, 0.20f), Offset(0.28f, 0.50f), Offset(0.78f, 0.85f)),
                smallDots = listOf(Offset(0.30f, 0.15f), Offset(0.30f, 0.85f), Offset(0.68f, 0.45f), Offset(0.30f, 0.62f), Offset(0.72f, 0.85f)),
                isCapitalCurved = false,
                isSmallCurved = false
            ),
            // L: Straight lines
            DotLetterInfo('L', "Lion", "Lembu", "🦁",
                capitalDots = listOf(Offset(0.30f, 0.15f), Offset(0.30f, 0.85f), Offset(0.75f, 0.85f)),
                smallDots = listOf(Offset(0.50f, 0.15f), Offset(0.50f, 0.85f)),
                isCapitalCurved = false,
                isSmallCurved = false
            ),
            // M: Capital straight; Small curved arches
            DotLetterInfo('M', "Monkey", "Monyet", "🐵",
                capitalDots = listOf(Offset(0.20f, 0.85f), Offset(0.20f, 0.18f), Offset(0.50f, 0.58f), Offset(0.80f, 0.18f), Offset(0.80f, 0.85f)),
                smallDots = listOf(Offset(0.22f, 0.85f), Offset(0.22f, 0.45f), Offset(0.48f, 0.45f), Offset(0.48f, 0.85f), Offset(0.48f, 0.45f), Offset(0.78f, 0.45f), Offset(0.78f, 0.85f)),
                isCapitalCurved = false,
                isSmallCurved = true,
                smallStraightIndices = setOf(0, 2, 4)
            ),
            // N: Capital straight; Small curved arch
            DotLetterInfo('N', "Nest", "Nanas", "🪺",
                capitalDots = listOf(Offset(0.25f, 0.85f), Offset(0.25f, 0.18f), Offset(0.75f, 0.85f), Offset(0.75f, 0.18f)),
                smallDots = listOf(Offset(0.28f, 0.85f), Offset(0.28f, 0.45f), Offset(0.55f, 0.42f), Offset(0.72f, 0.60f), Offset(0.72f, 0.85f)),
                isCapitalCurved = false,
                isSmallCurved = true,
                smallStraightIndices = setOf(0)
            ),
            // O: Closed smooth circle
            DotLetterInfo('O', "Orange", "Oren", "🍊",
                capitalDots = listOf(Offset(0.50f, 0.15f), Offset(0.22f, 0.50f), Offset(0.50f, 0.85f), Offset(0.78f, 0.50f), Offset(0.50f, 0.15f)),
                smallDots = listOf(Offset(0.50f, 0.40f), Offset(0.28f, 0.62f), Offset(0.50f, 0.85f), Offset(0.72f, 0.62f), Offset(0.50f, 0.40f)),
                isCapitalCurved = true,
                isSmallCurved = true
            ),
            // P: Straight spine + curved loop
            DotLetterInfo('P', "Penguin", "Pisang", "🐧",
                capitalDots = listOf(Offset(0.28f, 0.85f), Offset(0.28f, 0.18f), Offset(0.68f, 0.22f), Offset(0.72f, 0.45f), Offset(0.28f, 0.52f)),
                smallDots = listOf(Offset(0.30f, 0.40f), Offset(0.30f, 0.95f), Offset(0.65f, 0.45f), Offset(0.70f, 0.65f), Offset(0.30f, 0.72f)),
                isCapitalCurved = true,
                isSmallCurved = true,
                capitalStraightIndices = setOf(0),
                smallStraightIndices = setOf(0)
            ),
            // Q: Oval + tail
            DotLetterInfo('Q', "Queen", "Quran", "👑",
                capitalDots = listOf(Offset(0.50f, 0.15f), Offset(0.22f, 0.50f), Offset(0.50f, 0.85f), Offset(0.78f, 0.50f), Offset(0.50f, 0.15f), Offset(0.58f, 0.68f), Offset(0.82f, 0.88f)),
                smallDots = listOf(Offset(0.68f, 0.40f), Offset(0.38f, 0.42f), Offset(0.28f, 0.62f), Offset(0.50f, 0.80f), Offset(0.68f, 0.62f), Offset(0.68f, 0.95f)),
                isCapitalCurved = true,
                isSmallCurved = true,
                capitalStraightIndices = setOf(5),
                smallStraightIndices = setOf(4)
            ),
            // R: Straight spine + curved top loop + straight leg
            DotLetterInfo('R', "Rabbit", "Rusa", "🐰",
                capitalDots = listOf(Offset(0.25f, 0.85f), Offset(0.25f, 0.18f), Offset(0.68f, 0.22f), Offset(0.68f, 0.48f), Offset(0.25f, 0.52f), Offset(0.75f, 0.85f)),
                smallDots = listOf(Offset(0.35f, 0.85f), Offset(0.35f, 0.45f), Offset(0.55f, 0.40f), Offset(0.75f, 0.48f)),
                isCapitalCurved = true,
                isSmallCurved = true,
                capitalStraightIndices = setOf(0, 4),
                smallStraightIndices = setOf(0)
            ),
            // S: Smooth S-curve
            DotLetterInfo('S', "Sun", "Singa", "☀️",
                capitalDots = listOf(Offset(0.75f, 0.28f), Offset(0.50f, 0.15f), Offset(0.28f, 0.35f), Offset(0.50f, 0.50f), Offset(0.72f, 0.65f), Offset(0.48f, 0.85f), Offset(0.25f, 0.75f)),
                smallDots = listOf(Offset(0.70f, 0.48f), Offset(0.50f, 0.40f), Offset(0.35f, 0.52f), Offset(0.52f, 0.65f), Offset(0.68f, 0.75f), Offset(0.48f, 0.85f), Offset(0.30f, 0.78f)),
                isCapitalCurved = true,
                isSmallCurved = true
            ),
            // T: Straight lines
            DotLetterInfo('T', "Tiger", "Topi", "🐯",
                capitalDots = listOf(Offset(0.20f, 0.18f), Offset(0.80f, 0.18f), Offset(0.50f, 0.18f), Offset(0.50f, 0.85f)),
                smallDots = listOf(Offset(0.45f, 0.18f), Offset(0.45f, 0.82f), Offset(0.65f, 0.85f), Offset(0.28f, 0.42f), Offset(0.68f, 0.42f)),
                isCapitalCurved = false,
                isSmallCurved = true,
                smallStraightIndices = setOf(0, 2)
            ),
            // U: U-curve
            DotLetterInfo('U', "Umbrella", "Udang", "☂️",
                capitalDots = listOf(Offset(0.25f, 0.18f), Offset(0.25f, 0.68f), Offset(0.50f, 0.85f), Offset(0.75f, 0.68f), Offset(0.75f, 0.18f)),
                smallDots = listOf(Offset(0.30f, 0.42f), Offset(0.30f, 0.72f), Offset(0.52f, 0.85f), Offset(0.70f, 0.72f), Offset(0.70f, 0.42f), Offset(0.70f, 0.85f)),
                isCapitalCurved = true,
                isSmallCurved = true,
                capitalStraightIndices = setOf(0, 3)
            ),
            // V: Straight V-shape
            DotLetterInfo('V', "Van", "Van", "🚐",
                capitalDots = listOf(Offset(0.22f, 0.18f), Offset(0.50f, 0.85f), Offset(0.78f, 0.18f)),
                smallDots = listOf(Offset(0.28f, 0.42f), Offset(0.50f, 0.85f), Offset(0.72f, 0.42f)),
                isCapitalCurved = false,
                isSmallCurved = false
            ),
            // W: Straight W-shape
            DotLetterInfo('W', "Whale", "Wau", "🐋",
                capitalDots = listOf(Offset(0.18f, 0.18f), Offset(0.35f, 0.85f), Offset(0.50f, 0.45f), Offset(0.65f, 0.85f), Offset(0.82f, 0.18f)),
                smallDots = listOf(Offset(0.22f, 0.42f), Offset(0.38f, 0.85f), Offset(0.50f, 0.58f), Offset(0.62f, 0.85f), Offset(0.78f, 0.42f)),
                isCapitalCurved = false,
                isSmallCurved = false
            ),
            // X: Straight lines
            DotLetterInfo('X', "Xylophone", "Xilofon", "🎹",
                capitalDots = listOf(Offset(0.22f, 0.18f), Offset(0.78f, 0.85f), Offset(0.50f, 0.51f), Offset(0.78f, 0.18f), Offset(0.22f, 0.85f)),
                smallDots = listOf(Offset(0.28f, 0.42f), Offset(0.72f, 0.85f), Offset(0.50f, 0.63f), Offset(0.72f, 0.42f), Offset(0.28f, 0.85f)),
                isCapitalCurved = false,
                isSmallCurved = false
            ),
            // Y: Straight lines
            DotLetterInfo('Y', "Yacht", "Yo-yo", "⛵",
                capitalDots = listOf(Offset(0.22f, 0.18f), Offset(0.50f, 0.50f), Offset(0.78f, 0.18f), Offset(0.50f, 0.50f), Offset(0.50f, 0.85f)),
                smallDots = listOf(Offset(0.28f, 0.42f), Offset(0.50f, 0.70f), Offset(0.72f, 0.42f), Offset(0.50f, 0.70f), Offset(0.40f, 0.95f)),
                isCapitalCurved = false,
                isSmallCurved = false
            ),
            // Z: Straight lines
            DotLetterInfo('Z', "Zebra", "Zirafah", "🦓",
                capitalDots = listOf(Offset(0.25f, 0.18f), Offset(0.75f, 0.18f), Offset(0.25f, 0.85f), Offset(0.75f, 0.85f)),
                smallDots = listOf(Offset(0.30f, 0.45f), Offset(0.70f, 0.45f), Offset(0.30f, 0.85f), Offset(0.70f, 0.85f)),
                isCapitalCurved = false,
                isSmallCurved = false
            )
        )
    }

    val currentItem = letters[selectedLetterIndex]
    val currentDots = if (isCapital) currentItem.capitalDots else currentItem.smallDots
    val letterDisplay = if (isCapital) currentItem.letter.uppercaseChar() else currentItem.letter.lowercaseChar()
    val word = if (LanguageManager.isMalay()) currentItem.bmWord else currentItem.enWord

    // Reset when letter or mode changes
    LaunchedEffect(selectedLetterIndex, isCapital) {
        completedDotsCount = 0
        isCompleted = false
        SpeechEngine.speak(
            if (LanguageManager.isMalay())
                "Sambung titik untuk huruf $letterDisplay!"
            else
                "Connect the dots for letter $letterDisplay!"
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
                confettiState.spawn(300f, 300f, 45)
                SpeechEngine.speak(
                    if (LanguageManager.isMalay())
                        "Hebat! Huruf $letterDisplay! $letterDisplay untuk $word! ${currentItem.emoji}"
                    else
                        "Awesome! Letter $letterDisplay! $letterDisplay is for $word! ${currentItem.emoji}"
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
            // Header with language and sound button
            ToddlerAppHeader(
                title = LanguageManager.abcDotsTitle(),
                subtitle = LanguageManager.abcDotsSubtitle(),
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(6.dp))

            // TOP ROW: Capital vs Small Letter Toggle Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Capital Option Pill
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

                Spacer(modifier = Modifier.width(10.dp))

                // Small Option Pill
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

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Scrollable Alphabet Selector Bar
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(letters) { index, item ->
                    val isSelected = index == selectedLetterIndex
                    val charShow = if (isCapital) item.letter.uppercaseChar() else item.letter.lowercaseChar()

                    Box(
                        modifier = Modifier
                            .size(if (isTablet) 46.dp else 40.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) (if (isCapital) SkyVibrant else CandyCoral) else Color.White.copy(alpha = 0.9f))
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

            Spacer(modifier = Modifier.height(10.dp))

            // CENTER: Interactive Canvas Card
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
                    // Canvas with Dots and Tracing Lines
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .pointerInput(selectedLetterIndex, isCapital, completedDotsCount) {
                                detectTapGestures { tapOffset ->
                                    val canvasW = size.width
                                    val canvasH = size.height
                                    val nextDotIdx = completedDotsCount
                                    if (nextDotIdx < currentDots.size) {
                                        val targetPt = currentDots[nextDotIdx]
                                        val actualPt = Offset(targetPt.x * canvasW, targetPt.y * canvasH)
                                        val dist = hypot(tapOffset.x - actualPt.x, tapOffset.y - actualPt.y)
                                        if (dist < 55.dp.toPx()) {
                                            handleConnectDot(nextDotIdx)
                                        }
                                    }
                                }
                            }
                            .pointerInput(selectedLetterIndex, isCapital, completedDotsCount) {
                                detectDragGestures { change, _ ->
                                    val canvasW = size.width
                                    val canvasH = size.height
                                    val nextDotIdx = completedDotsCount
                                    if (nextDotIdx < currentDots.size) {
                                        val targetPt = currentDots[nextDotIdx]
                                        val actualPt = Offset(targetPt.x * canvasW, targetPt.y * canvasH)
                                        val dist = hypot(change.position.x - actualPt.x, change.position.y - actualPt.y)
                                        if (dist < 55.dp.toPx()) {
                                            handleConnectDot(nextDotIdx)
                                        }
                                    }
                                }
                            }
                    ) {
                        val w = size.width
                        val h = size.height

                        val isCurrentCurved = if (isCapital) currentItem.isCapitalCurved else currentItem.isSmallCurved
                        val currentStraightIndices = if (isCapital) currentItem.capitalStraightIndices else currentItem.smallStraightIndices

                        // 1. Draw dashed guide outline connecting dots
                        if (currentDots.size > 1) {
                            val guidePath = buildSmoothPath(
                                dots = currentDots,
                                w = w,
                                h = h,
                                count = currentDots.size,
                                isCurved = isCurrentCurved,
                                straightIndices = currentStraightIndices
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

                        // 2. Draw connected solid strokes between completed dots
                        if (completedDotsCount > 1) {
                            val strokePath = buildSmoothPath(
                                dots = currentDots,
                                w = w,
                                h = h,
                                count = completedDotsCount,
                                isCurved = isCurrentCurved,
                                straightIndices = currentStraightIndices
                            )
                            drawPath(
                                path = strokePath,
                                color = if (isCapital) SkyVibrant else CandyCoral,
                                style = Stroke(
                                    width = 7.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            )
                        }

                        // 3. Draw dots
                        currentDots.forEachIndexed { index, normPt ->
                            val pt = Offset(normPt.x * w, normPt.y * h)
                            val isConnected = index < completedDotsCount
                            val isNext = index == completedDotsCount

                            if (isNext) {
                                // Pulsating glow circle on the next dot to connect
                                drawCircle(
                                    color = SunnyYellow.copy(alpha = 0.55f),
                                    radius = 24.dp.toPx() * pulseScale,
                                    center = pt
                                )
                            }

                            // Dot background
                            drawCircle(
                                color = if (isConnected) (if (isCapital) SkyVibrant else CandyCoral)
                                else if (isNext) SunnyYellow
                                else Color(0xFFE2E8F0),
                                radius = if (isNext) 18.dp.toPx() else 14.dp.toPx(),
                                center = pt
                            )

                            // White center
                            drawCircle(
                                color = Color.White,
                                radius = if (isNext) 10.dp.toPx() else 7.dp.toPx(),
                                center = pt
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
                                        Text(
                                            text = "$letterDisplay untuk $word!",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TextDark
                                        )
                                        Text(
                                            text = if (LanguageManager.isMalay()) "Tahniah! Anda berjaya!" else "Hooray! Great job!",
                                            fontSize = 12.sp,
                                            color = SkyVibrant,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                ToddlerButton(
                                    text = "Seterusnya ▶",
                                    buttonColor = SkyVibrant,
                                    shadowColor = SkyShadow,
                                    height = 42.dp,
                                    onClick = {
                                        SoundEngine.playSparkle()
                                        selectedLetterIndex = (selectedLetterIndex + 1) % letters.size
                                    }
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

@Composable
private fun LetterCasePill(
    text: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isActive) activeColor else Color.White.copy(alpha = 0.9f))
            .border(
                1.5.dp,
                if (isActive) activeColor else Color(0xFFE2E8F0),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isActive) Color.White else TextDark
        )
    }
}

/**
 * Builds either a smooth Catmull-Rom curved path or straight polygon segments.
 * Pass straightIndices to keep specific segments straight.
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

