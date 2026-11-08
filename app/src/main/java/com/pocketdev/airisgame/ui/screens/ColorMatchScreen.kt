package com.pocketdev.airisgame.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import com.pocketdev.airisgame.ui.components.AppBackButton
import com.pocketdev.airisgame.ui.components.AppIconButton
import com.pocketdev.airisgame.ui.components.ConfettiOverlay
import com.pocketdev.airisgame.ui.components.ToddlerAppHeader
import com.pocketdev.airisgame.ui.components.rememberConfettiState
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.CoralShadow
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.SkyShadow
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark
import com.pocketdev.airisgame.ui.theme.YellowShadow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot

enum class PrimaryColorId {
    RED,
    BLUE,
    YELLOW
}

data class ColorMatchItem(
    val id: PrimaryColorId,
    val enName: String,
    val bmName: String,
    val emoji: String,
    val color: Color,
    val shadowColor: Color
)

@Composable
fun ColorMatchScreen(onBack: () -> Unit) {
    val confettiState = rememberConfettiState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val colorItems = remember {
        listOf(
            ColorMatchItem(
                PrimaryColorId.RED,
                "Red",
                "Merah",
                "🍎",
                CandyCoral,
                CoralShadow
            ),
            ColorMatchItem(
                PrimaryColorId.BLUE,
                "Blue",
                "Biru",
                "🐬",
                SkyVibrant,
                SkyShadow
            ),
            ColorMatchItem(
                PrimaryColorId.YELLOW,
                "Yellow",
                "Kuning",
                "⭐",
                SunnyYellow,
                YellowShadow
            )
        )
    }

    // Matched state for each of the 3 colors
    val matchedColors = remember { mutableStateMapOf<PrimaryColorId, Boolean>() }
    // Slot center positions in root coordinates
    val slotCenters = remember { mutableStateMapOf<PrimaryColorId, Offset>() }
    // Draggable home center positions in root coordinates
    val homeCenters = remember { mutableStateMapOf<PrimaryColorId, Offset>() }

    var allMatchedCelebration by remember { mutableStateOf(false) }

    fun resetGame() {
        matchedColors.clear()
        allMatchedCelebration = false
    }

    // Check when all 3 colors are snapped into place
    LaunchedEffect(matchedColors.size) {
        if (matchedColors.size == 3 && matchedColors.values.all { it } && !allMatchedCelebration) {
            allMatchedCelebration = true
            SoundEngine.playCelebrationFanfare()
            val celebrationText = if (LanguageManager.isMalay())
                "Hebat Airis! Semua warna berjaya dipadankan!"
            else
                "Hooray Airis! All colors matched!"
            SpeechEngine.speak(celebrationText)
            confettiState.spawn(350f, 350f, 60)

            // Auto reset after 3 seconds for continuous toddler play
            delay(3200L)
            resetGame()
        }
    }

    // Initial greeting
    LaunchedEffect(LanguageManager.currentLanguage) {
        val prompt = if (LanguageManager.isMalay())
            "Tarik warna merah, biru, dan kuning ke dalam bakul yang sepadan!"
        else
            "Drag red, blue, and yellow into the matching baskets!"
        SpeechEngine.speak(prompt)
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
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Universal Header with integrated Language Switcher and Sound Settings Button
            ToddlerAppHeader(
                title = LanguageManager.colorMatchTitle(),
                subtitle = if (LanguageManager.isMalay()) "Tarik & Lepas Magnetik" else "Magnetic Snap Matching",
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

            Spacer(modifier = Modifier.height(10.dp))

            // Guidance Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.94f))
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (LanguageManager.isMalay())
                        "🧺 Masukkan setiap bentuk ke dalam bakul warna yang sama!"
                    else
                        "🧺 Put each shape into the matching color basket!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TOP SECTION: 3 Target Silhouette Cutout Slots
            Text(
                text = if (LanguageManager.isMalay()) "🎯 Bakul Warna (Merah, Biru, Kuning)" else "🎯 Color Baskets (Red, Blue, Yellow)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                colorItems.forEach { item ->
                    val isMatched = matchedColors[item.id] == true
                    val colorName = if (LanguageManager.isMalay()) item.bmName else item.enName

                    TargetSlot(
                        item = item,
                        displayName = colorName,
                        isMatched = isMatched,
                        modifier = Modifier
                            .weight(1f)
                            .onGloballyPositioned { coordinates ->
                                val bounds = coordinates.boundsInRoot()
                                slotCenters[item.id] = bounds.center
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // BOTTOM SECTION: 3 Draggable Shapes with Magnetic Snapping
            Text(
                text = if (LanguageManager.isMalay()) "✨ Tarik Bentuk Ini" else "✨ Drag These Shapes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                colorItems.forEach { item ->
                    val isMatched = matchedColors[item.id] == true
                    val colorName = if (LanguageManager.isMalay()) item.bmName else item.enName

                    DraggableColorCard(
                        item = item,
                        displayName = colorName,
                        isMatched = isMatched,
                        modifier = Modifier
                            .weight(1f)
                            .onGloballyPositioned { coordinates ->
                                val bounds = coordinates.boundsInRoot()
                                homeCenters[item.id] = bounds.center
                            },
                        onDragReleased = { dropOffsetInRoot ->
                            val targetSlotCenter = slotCenters[item.id]
                            if (targetSlotCenter != null) {
                                val distance = hypot(
                                    dropOffsetInRoot.x - targetSlotCenter.x,
                                    dropOffsetInRoot.y - targetSlotCenter.y
                                )
                                val snapThreshold = with(density) { 105.dp.toPx() } // Generous magnetic snap zone

                                if (distance < snapThreshold) {
                                    // Magnetic Snap Success!
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
                                    // Wrong drop or dropped outside: play soft friendly chime & slide back
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
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isMatched) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f)
    )

    // Vibrant pastel container background colored specifically to match the item's color!
    val slotBgColor = if (isMatched) item.color.copy(alpha = 0.28f) else item.color.copy(alpha = 0.18f)
    val slotBorderColor = if (isMatched) item.color else item.color.copy(alpha = 0.70f)

    Box(
        modifier = modifier
            .height(150.dp)
            .scale(scale)
            .clip(RoundedCornerShape(26.dp))
            .background(slotBgColor)
            .border(
                width = if (isMatched) 3.5.dp else 2.5.dp,
                color = slotBorderColor,
                shape = RoundedCornerShape(26.dp)
            )
            .padding(8.dp)
            .semantics { contentDescription = "Slot for $displayName" },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isMatched) {
                // Snapped inside basket celebration
                Text(
                    text = item.emoji,
                    fontSize = 50.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(item.color)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "✓ $displayName",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            } else {
                // Unmistakable colored basket container matching the exact color
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.92f))
                        .border(2.5.dp, item.color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Ghost silhouette of the emoji with colored background so toddler sees what goes here
                    Text(
                        text = item.emoji,
                        fontSize = 36.sp,
                        modifier = Modifier.alpha(0.50f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bold colored badge pill with color dot + text
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(item.color)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = displayName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Draggable Primary Color Shape with Magnetic Snapping Physics
 */
@Composable
private fun DraggableColorCard(
    item: ColorMatchItem,
    displayName: String,
    isMatched: Boolean,
    modifier: Modifier = Modifier,
    onDragReleased: (Offset) -> Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val dragOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var isDragging by remember { mutableStateOf(false) }
    var cardRootCenter by remember { mutableStateOf(Offset.Zero) }

    val dragScale by animateFloatAsState(
        targetValue = if (isDragging) 1.18f else 1.0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 450f)
    )

    // When reset, animate dragOffset back home
    LaunchedEffect(isMatched) {
        if (!isMatched) {
            dragOffset.snapTo(Offset.Zero)
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                cardRootCenter = coordinates.boundsInRoot().center
            },
        contentAlignment = Alignment.Center
    ) {
        if (!isMatched) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .offset { IntOffset(dragOffset.value.x.toInt(), dragOffset.value.y.toInt()) }
                    .scale(dragScale)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                isDragging = true
                                SoundEngine.playBoing()
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                coroutineScope.launch {
                                    dragOffset.snapTo(dragOffset.value + dragAmount)
                                }
                            },
                            onDragEnd = {
                                isDragging = false
                                val currentRootPos = cardRootCenter + dragOffset.value
                                val snapped = onDragReleased(currentRootPos)
                                if (!snapped) {
                                    // Smoothly slide back home with spring physics
                                    coroutineScope.launch {
                                        dragOffset.animateTo(
                                            Offset.Zero,
                                            spring(dampingRatio = 0.6f, stiffness = 400f)
                                        )
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
                    .semantics { contentDescription = "Draggable $displayName ${item.emoji}" },
                contentAlignment = Alignment.Center
            ) {
                // Shadow base layer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = if (isDragging) 8.dp else 4.dp)
                        .background(item.shadowColor, RoundedCornerShape(26.dp))
                )

                // Main Draggable Card Surface
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = item.color),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = item.emoji,
                            fontSize = 50.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = displayName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            // Placeholder when shape is successfully snapped into top slot
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color.White.copy(alpha = 0.35f))
                    .border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(26.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✨",
                    fontSize = 32.sp
                )
            }
        }
    }
}
