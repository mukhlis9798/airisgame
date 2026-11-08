package com.pocketdev.airisgame.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.core.LanguageManager
import com.pocketdev.airisgame.ui.components.AppIconButton
import com.pocketdev.airisgame.ui.components.ConfettiOverlay
import com.pocketdev.airisgame.ui.components.ToddlerAppHeader
import com.pocketdev.airisgame.ui.components.rememberConfettiState
import com.pocketdev.airisgame.ui.theme.BubblePink
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.CoralShadow
import com.pocketdev.airisgame.ui.theme.LavenderShadow
import com.pocketdev.airisgame.ui.theme.OrangeShadow
import com.pocketdev.airisgame.ui.theme.PinkShadow
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.SkyShadow
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark
import com.pocketdev.airisgame.ui.theme.WarmOrange
import com.pocketdev.airisgame.ui.theme.YellowShadow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot

enum class RainbowColorId {
    RED,
    ORANGE,
    YELLOW,
    GREEN,
    BLUE,
    PURPLE,
    PINK
}

enum class ColorGameRound(val labelEn: String, val labelBm: String) {
    PRIMARY("Round 1 (3 Colors)", "Pusingan 1 (3 Warna)"),
    SECONDARY("Round 2 (4 Colors)", "Pusingan 2 (4 Warna)"),
    RAINBOW("7 Rainbow Colors", "7 Warna Pelangi")
}

data class ColorMatchItem(
    val id: RainbowColorId,
    val enName: String,
    val bmName: String,
    val emoji: String,
    val color: Color,
    val shadowColor: Color
)

@Composable
fun ColorMatchScreen(onBack: () -> Unit) {
    val confettiState = rememberConfettiState()
    val density = LocalDensity.current

    // All 7 vibrant rainbow colors with toddler emojis
    val allRainbowItems = remember {
        listOf(
            ColorMatchItem(
                id = RainbowColorId.RED,
                enName = "Red",
                bmName = "Merah",
                emoji = "🍎",
                color = CandyCoral,
                shadowColor = CoralShadow
            ),
            ColorMatchItem(
                id = RainbowColorId.ORANGE,
                enName = "Orange",
                bmName = "Oren",
                emoji = "🍊",
                color = WarmOrange,
                shadowColor = OrangeShadow
            ),
            ColorMatchItem(
                id = RainbowColorId.YELLOW,
                enName = "Yellow",
                bmName = "Kuning",
                emoji = "⭐",
                color = SunnyYellow,
                shadowColor = YellowShadow
            ),
            ColorMatchItem(
                id = RainbowColorId.GREEN,
                enName = "Green",
                bmName = "Hijau",
                emoji = "🐸",
                color = Color(0xFF4CAF50),
                shadowColor = Color(0xFF2E7D32)
            ),
            ColorMatchItem(
                id = RainbowColorId.BLUE,
                enName = "Blue",
                bmName = "Biru",
                emoji = "🐬",
                color = SkyVibrant,
                shadowColor = SkyShadow
            ),
            ColorMatchItem(
                id = RainbowColorId.PURPLE,
                enName = "Purple",
                bmName = "Ungu",
                emoji = "🍇",
                color = Color(0xFFAB47BC),
                shadowColor = LavenderShadow
            ),
            ColorMatchItem(
                id = RainbowColorId.PINK,
                enName = "Pink",
                bmName = "Merah Jambu",
                emoji = "🌸",
                color = BubblePink,
                shadowColor = PinkShadow
            )
        )
    }

    var currentRound by remember { mutableStateOf(ColorGameRound.PRIMARY) }

    val currentItems = remember(currentRound) {
        when (currentRound) {
            ColorGameRound.PRIMARY -> listOf(
                allRainbowItems[0], // Red 🍎
                allRainbowItems[4], // Blue 🐬
                allRainbowItems[2]  // Yellow ⭐
            )
            ColorGameRound.SECONDARY -> listOf(
                allRainbowItems[3], // Green 🐸
                allRainbowItems[1], // Orange 🍊
                allRainbowItems[5], // Purple 🍇
                allRainbowItems[6]  // Pink 🌸
            )
            ColorGameRound.RAINBOW -> allRainbowItems // All 7 Colors!
        }
    }

    // Matched state for each active color
    val matchedColors = remember { mutableStateMapOf<RainbowColorId, Boolean>() }
    // Slot center positions in root coordinates
    val slotCenters = remember { mutableStateMapOf<RainbowColorId, Offset>() }
    // Draggable card root positions
    val homeCenters = remember { mutableStateMapOf<RainbowColorId, Offset>() }

    var allMatchedCelebration by remember { mutableStateOf(false) }

    fun resetGame() {
        matchedColors.clear()
        allMatchedCelebration = false
    }

    // Check completion of the current round
    LaunchedEffect(matchedColors.size, currentRound) {
        val totalRequired = currentItems.size
        val allCompleted = currentItems.all { matchedColors[it.id] == true }
        if (allCompleted && !allMatchedCelebration && totalRequired > 0) {
            allMatchedCelebration = true
            SoundEngine.playCelebrationFanfare()
            val celebrationText = if (LanguageManager.isMalay())
                "Hebat Airis! Semua $totalRequired warna berjaya dipadankan!"
            else
                "Hooray Airis! All $totalRequired colors matched!"
            SpeechEngine.speak(celebrationText)
            confettiState.spawn(350f, 350f, 75)

            // Auto-advance to next round after celebration
            delay(3300L)
            currentRound = when (currentRound) {
                ColorGameRound.PRIMARY -> ColorGameRound.SECONDARY
                ColorGameRound.SECONDARY -> ColorGameRound.RAINBOW
                ColorGameRound.RAINBOW -> ColorGameRound.PRIMARY
            }
            resetGame()
        }
    }

    // Voice instructions upon round change or language toggle
    LaunchedEffect(currentRound, LanguageManager.currentLanguage) {
        val prompt = when (currentRound) {
            ColorGameRound.PRIMARY -> if (LanguageManager.isMalay())
                "Tarik atau sentuh warna merah, biru, dan kuning ke dalam bakul!"
            else
                "Drag or tap red, blue, and yellow into the matching baskets!"
            ColorGameRound.SECONDARY -> if (LanguageManager.isMalay())
                "Tarik atau sentuh warna hijau, oren, ungu, dan merah jambu!"
            else
                "Drag or tap green, orange, purple, and pink into the matching baskets!"
            ColorGameRound.RAINBOW -> if (LanguageManager.isMalay())
                "Wah! Jom padankan semua 7 warna pelangi yang cantik!"
            else
                "Wow! Let's match all 7 beautiful rainbow colors!"
        }
        SpeechEngine.speak(prompt)
    }

    val isFullRainbow = currentRound == ColorGameRound.RAINBOW

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            ToddlerAppHeader(
                title = LanguageManager.colorMatchTitle(),
                subtitle = if (LanguageManager.isMalay()) "Tarik & Padankan Warna" else "Magnetic Snap Matching",
                onBack = onBack,
                actionSlot = {
                    AppIconButton(
                        icon = Icons.Rounded.Refresh,
                        contentDescription = LanguageManager.again(),
                        size = 38.dp,
                        iconSize = 22.dp,
                        onClick = {
                            SoundEngine.playSparkle()
                            resetGame()
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Round Switcher Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColorGameRound.values().forEach { round ->
                    val isSelected = currentRound == round
                    val roundText = if (LanguageManager.isMalay()) round.labelBm else round.labelEn
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CandyCoral else Color.White.copy(alpha = 0.85f))
                            .border(
                                1.5.dp,
                                if (isSelected) CoralShadow else Color(0xFFCBD5E1),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                SoundEngine.playSparkle()
                                currentRound = round
                                resetGame()
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = roundText,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) Color.White else TextDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Guidance Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.94f))
                    .padding(vertical = 7.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (LanguageManager.isMalay())
                        "🧺 Tarik atau sentuh bentuk ke dalam bakul warna yang sama!"
                    else
                        "🧺 Drag or tap each shape into the matching color basket!",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SECTION 1: Target Baskets
            Text(
                text = if (LanguageManager.isMalay()) "🎯 Bakul Warna" else "🎯 Color Baskets",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (!isFullRainbow) {
                // 3 or 4 baskets in a single row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentItems.forEach { item ->
                        val isMatched = matchedColors[item.id] == true
                        val colorName = if (LanguageManager.isMalay()) item.bmName else item.enName

                        TargetSlot(
                            item = item,
                            displayName = colorName,
                            isMatched = isMatched,
                            compact = false,
                            modifier = Modifier
                                .weight(1f)
                                .onGloballyPositioned { coordinates ->
                                    slotCenters[item.id] = coordinates.boundsInRoot().center
                                }
                        )
                    }
                }
            } else {
                // 7 baskets in 2 rows (4 on top, 3 below centered)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currentItems.take(4).forEach { item ->
                            val isMatched = matchedColors[item.id] == true
                            val colorName = if (LanguageManager.isMalay()) item.bmName else item.enName
                            TargetSlot(
                                item = item,
                                displayName = colorName,
                                isMatched = isMatched,
                                compact = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .onGloballyPositioned { coordinates ->
                                        slotCenters[item.id] = coordinates.boundsInRoot().center
                                    }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(0.78f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currentItems.drop(4).forEach { item ->
                            val isMatched = matchedColors[item.id] == true
                            val colorName = if (LanguageManager.isMalay()) item.bmName else item.enName
                            TargetSlot(
                                item = item,
                                displayName = colorName,
                                isMatched = isMatched,
                                compact = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .onGloballyPositioned { coordinates ->
                                        slotCenters[item.id] = coordinates.boundsInRoot().center
                                    }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION 2: Draggable / Tap-to-Snap Color Shapes
            Text(
                text = if (LanguageManager.isMalay()) "✨ Bentuk Warna (Tarik / Sentuh)" else "✨ Color Shapes (Drag / Tap)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (!isFullRainbow) {
                // 3 or 4 cards in a single row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    currentItems.forEach { item ->
                        val isMatched = matchedColors[item.id] == true
                        val colorName = if (LanguageManager.isMalay()) item.bmName else item.enName

                        DraggableColorCard(
                            item = item,
                            displayName = colorName,
                            isMatched = isMatched,
                            compact = false,
                            modifier = Modifier
                                .weight(1f)
                                .onGloballyPositioned { coordinates ->
                                    homeCenters[item.id] = coordinates.boundsInRoot().center
                                },
                            onSnap = {
                                matchedColors[item.id] = true
                                SoundEngine.playPop()
                                SoundEngine.playSparkle()
                                SpeechEngine.speak(
                                    if (LanguageManager.isMalay())
                                        "$colorName! Padanan tepat!"
                                    else
                                        "$colorName! Correct!"
                                )
                                slotCenters[item.id]?.let { center ->
                                    confettiState.spawn(center.x, center.y, 25)
                                }
                            },
                            onDragReleased = { dropOffsetInRoot ->
                                val targetSlotCenter = slotCenters[item.id]
                                if (targetSlotCenter != null) {
                                    val distance = hypot(
                                        dropOffsetInRoot.x - targetSlotCenter.x,
                                        dropOffsetInRoot.y - targetSlotCenter.y
                                    )
                                    val snapThreshold = with(density) { 95.dp.toPx() }
                                    if (distance < snapThreshold) {
                                        matchedColors[item.id] = true
                                        SoundEngine.playPop()
                                        SoundEngine.playSparkle()
                                        SpeechEngine.speak(
                                            if (LanguageManager.isMalay())
                                                "$colorName! Padanan tepat!"
                                            else
                                                "$colorName! Correct!"
                                        )
                                        confettiState.spawn(targetSlotCenter.x, targetSlotCenter.y, 25)
                                        true
                                    } else {
                                        SoundEngine.playWobble()
                                        SpeechEngine.speak(
                                            if (LanguageManager.isMalay()) "Cuba lagi!" else "Try again!"
                                        )
                                        false
                                    }
                                } else {
                                    false
                                }
                            }
                        )
                    }
                }
            } else {
                // 7 cards in 2 rows
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currentItems.take(4).forEach { item ->
                            val isMatched = matchedColors[item.id] == true
                            val colorName = if (LanguageManager.isMalay()) item.bmName else item.enName
                            DraggableColorCard(
                                item = item,
                                displayName = colorName,
                                isMatched = isMatched,
                                compact = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .onGloballyPositioned { coordinates ->
                                        homeCenters[item.id] = coordinates.boundsInRoot().center
                                    },
                                onSnap = {
                                    matchedColors[item.id] = true
                                    SoundEngine.playPop()
                                    SoundEngine.playSparkle()
                                    SpeechEngine.speak(
                                        if (LanguageManager.isMalay())
                                            "$colorName! Padanan tepat!"
                                        else
                                            "$colorName! Correct!"
                                    )
                                    slotCenters[item.id]?.let { center ->
                                        confettiState.spawn(center.x, center.y, 20)
                                    }
                                },
                                onDragReleased = { dropOffsetInRoot ->
                                    val targetSlotCenter = slotCenters[item.id]
                                    if (targetSlotCenter != null) {
                                        val distance = hypot(
                                            dropOffsetInRoot.x - targetSlotCenter.x,
                                            dropOffsetInRoot.y - targetSlotCenter.y
                                        )
                                        val snapThreshold = with(density) { 85.dp.toPx() }
                                        if (distance < snapThreshold) {
                                            matchedColors[item.id] = true
                                            SoundEngine.playPop()
                                            SoundEngine.playSparkle()
                                            SpeechEngine.speak(
                                                if (LanguageManager.isMalay())
                                                    "$colorName! Padanan tepat!"
                                                else
                                                    "$colorName! Correct!"
                                            )
                                            confettiState.spawn(targetSlotCenter.x, targetSlotCenter.y, 20)
                                            true
                                        } else {
                                            SoundEngine.playWobble()
                                            SpeechEngine.speak(
                                                if (LanguageManager.isMalay()) "Cuba lagi!" else "Try again!"
                                            )
                                            false
                                        }
                                    } else {
                                        false
                                    }
                                }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(0.78f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currentItems.drop(4).forEach { item ->
                            val isMatched = matchedColors[item.id] == true
                            val colorName = if (LanguageManager.isMalay()) item.bmName else item.enName
                            DraggableColorCard(
                                item = item,
                                displayName = colorName,
                                isMatched = isMatched,
                                compact = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .onGloballyPositioned { coordinates ->
                                        homeCenters[item.id] = coordinates.boundsInRoot().center
                                    },
                                onSnap = {
                                    matchedColors[item.id] = true
                                    SoundEngine.playPop()
                                    SoundEngine.playSparkle()
                                    SpeechEngine.speak(
                                        if (LanguageManager.isMalay())
                                            "$colorName! Padanan tepat!"
                                        else
                                            "$colorName! Correct!"
                                    )
                                    slotCenters[item.id]?.let { center ->
                                        confettiState.spawn(center.x, center.y, 20)
                                    }
                                },
                                onDragReleased = { dropOffsetInRoot ->
                                    val targetSlotCenter = slotCenters[item.id]
                                    if (targetSlotCenter != null) {
                                        val distance = hypot(
                                            dropOffsetInRoot.x - targetSlotCenter.x,
                                            dropOffsetInRoot.y - targetSlotCenter.y
                                        )
                                        val snapThreshold = with(density) { 85.dp.toPx() }
                                        if (distance < snapThreshold) {
                                            matchedColors[item.id] = true
                                            SoundEngine.playPop()
                                            SoundEngine.playSparkle()
                                            SpeechEngine.speak(
                                                if (LanguageManager.isMalay())
                                                    "$colorName! Padanan tepat!"
                                                else
                                                    "$colorName! Correct!"
                                            )
                                            confettiState.spawn(targetSlotCenter.x, targetSlotCenter.y, 20)
                                            true
                                        } else {
                                            SoundEngine.playWobble()
                                            SpeechEngine.speak(
                                                if (LanguageManager.isMalay()) "Cuba lagi!" else "Try again!"
                                            )
                                            false
                                        }
                                    } else {
                                        false
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        ConfettiOverlay(state = confettiState)
    }
}

/**
 * Silhouette Target Slot / Basket
 */
@Composable
private fun TargetSlot(
    item: ColorMatchItem,
    displayName: String,
    isMatched: Boolean,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isMatched) 1.06f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f)
    )

    val slotBgColor = if (isMatched) item.color.copy(alpha = 0.28f) else item.color.copy(alpha = 0.18f)
    val slotBorderColor = if (isMatched) item.color else item.color.copy(alpha = 0.70f)
    val heightDp = if (compact) 82.dp else 128.dp

    Box(
        modifier = modifier
            .height(heightDp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(slotBgColor)
            .border(
                width = if (isMatched) 3.dp else 2.dp,
                color = slotBorderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(4.dp)
            .semantics { contentDescription = "Slot for $displayName" },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isMatched) {
                Text(
                    text = item.emoji,
                    fontSize = if (compact) 26.sp else 40.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(item.color)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "✓ $displayName",
                        fontSize = if (compact) 10.sp else 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(if (compact) 38.dp else 52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.92f))
                        .border(2.dp, item.color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.emoji,
                        fontSize = if (compact) 22.sp else 30.sp,
                        modifier = Modifier.alpha(0.50f)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(item.color)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = displayName,
                        fontSize = if (compact) 10.sp else 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Draggable Primary & Rainbow Color Shape with Tap or Drag Magnetism
 */
@Composable
private fun DraggableColorCard(
    item: ColorMatchItem,
    displayName: String,
    isMatched: Boolean,
    compact: Boolean,
    modifier: Modifier = Modifier,
    onSnap: () -> Unit,
    onDragReleased: (Offset) -> Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val dragOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var isDragging by remember { mutableStateOf(false) }
    var cardRootCenter by remember { mutableStateOf(Offset.Zero) }

    val dragScale by animateFloatAsState(
        targetValue = if (isDragging) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 450f)
    )

    LaunchedEffect(isMatched) {
        if (!isMatched) {
            dragOffset.snapTo(Offset.Zero)
        }
    }

    val heightDp = if (compact) 82.dp else 128.dp

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                cardRootCenter = coordinates.boundsInRoot().center
            },
        contentAlignment = Alignment.Center
    ) {
        if (!isMatched) {
            var totalDrag by remember { mutableStateOf(Offset.Zero) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightDp)
                    .offset { IntOffset(dragOffset.value.x.toInt(), dragOffset.value.y.toInt()) }
                    .scale(dragScale)
                    .pointerInput(item.id, isMatched) {
                        detectDragGestures(
                            onDragStart = {
                                totalDrag = Offset.Zero
                                isDragging = true
                                SoundEngine.playBoing()
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                totalDrag += dragAmount
                                coroutineScope.launch {
                                    dragOffset.snapTo(dragOffset.value + dragAmount)
                                }
                            },
                            onDragEnd = {
                                isDragging = false
                                val currentRootPos = cardRootCenter + dragOffset.value
                                // If the toddler simply tapped (minimal drag movement), snap directly!
                                val isTap = hypot(totalDrag.x, totalDrag.y) < 14.dp.toPx()
                                if (isTap) {
                                    coroutineScope.launch {
                                        dragOffset.snapTo(Offset.Zero)
                                    }
                                    onSnap()
                                } else {
                                    val snapped = onDragReleased(currentRootPos)
                                    if (!snapped) {
                                        coroutineScope.launch {
                                            dragOffset.animateTo(
                                                Offset.Zero,
                                                spring(dampingRatio = 0.6f, stiffness = 400f)
                                            )
                                        }
                                    }
                                }
                            },
                            onDragCancel = {
                                isDragging = false
                                coroutineScope.launch {
                                    dragOffset.animateTo(Offset.Zero)
                                }
                            }
                        )
                    }
                    .semantics { contentDescription = "Card $displayName ${item.emoji}" },
                contentAlignment = Alignment.Center
            ) {
                // Shadow layer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = if (isDragging) 6.dp else 3.dp)
                        .background(item.shadowColor, RoundedCornerShape(20.dp))
                )

                // Main Card Surface
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = item.color),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = item.emoji,
                            fontSize = if (compact) 26.sp else 40.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = displayName,
                            fontSize = if (compact) 11.sp else 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            // Placeholder when matched
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightDp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.35f))
                    .border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✨",
                    fontSize = if (compact) 22.sp else 28.sp
                )
            }
        }
    }
}
