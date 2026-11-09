package com.pocketdev.airisgame.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.pocketdev.airisgame.ui.theme.FreshTeal
import com.pocketdev.airisgame.ui.theme.MagicLavender
import com.pocketdev.airisgame.ui.theme.MintFresh
import com.pocketdev.airisgame.ui.theme.MintShadow
import com.pocketdev.airisgame.ui.theme.PinkShadow
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.SkyShadow
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark
import com.pocketdev.airisgame.ui.theme.WarmOrange
import com.pocketdev.airisgame.ui.theme.YellowShadow

data class NumberItem(
    val number: Int,
    val wordEn: String,
    val wordBm: String,
    val emoji: String,
    val itemNameEn: String,
    val itemNameBm: String,
    val color: Color,
    val shadowColor: Color
)

@Composable
fun NumbersScreen(onBack: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val confettiState = rememberConfettiState()

    val numbersList = remember {
        listOf(
            NumberItem(1, "One", "Satu", "☀️", "Sun", "Matahari", CandyCoral, CoralShadow),
            NumberItem(2, "Two", "Dua", "🐱", "Cats", "Kucing", WarmOrange, CoralShadow),
            NumberItem(3, "Three", "Tiga", "🍎", "Apples", "Epal", SunnyYellow, YellowShadow),
            NumberItem(4, "Four", "Empat", "🚗", "Cars", "Kereta", MintFresh, MintShadow),
            NumberItem(5, "Five", "Lima", "⭐", "Stars", "Bintang", SkyVibrant, SkyShadow),
            NumberItem(6, "Six", "Enam", "🐬", "Dolphins", "Ikan Lumba-lumba", FreshTeal, SkyShadow),
            NumberItem(7, "Seven", "Tujuh", "🎈", "Balloons", "Belon", MagicLavender, Color(0xFF7C4DFF)),
            NumberItem(8, "Eight", "Lapan", "🌸", "Flowers", "Bunga", BubblePink, PinkShadow),
            NumberItem(9, "Nine", "Sembilan", "🍌", "Bananas", "Pisang", SunnyYellow, YellowShadow),
            NumberItem(10, "Ten", "Sepuluh", "🐝", "Bees", "Lebah", CandyCoral, CoralShadow)
        )
    }

    val malayNumberWords = listOf(
        "Satu", "Dua", "Tiga", "Empat", "Lima",
        "Enam", "Tujuh", "Lapan", "Sembilan", "Sepuluh"
    )
    val englishNumberWords = listOf(
        "One", "Two", "Three", "Four", "Five",
        "Six", "Seven", "Eight", "Nine", "Ten"
    )

    var selectedIndex by remember { mutableStateOf(0) }
    val currentItem = numbersList[selectedIndex]

    // Track which items have been counted/tapped (index 0 until number)
    val countedMap = remember(selectedIndex) { mutableStateMapOf<Int, Boolean>() }
    var celebrationPlayed by remember { mutableStateOf(false) }

    LaunchedEffect(selectedIndex, LanguageManager.currentLanguage) {
        celebrationPlayed = false
        val isMalay = LanguageManager.isMalay()
        val numWord = if (isMalay) currentItem.wordBm else currentItem.wordEn
        val itemWord = if (isMalay) currentItem.itemNameBm else currentItem.itemNameEn
        val speech = if (isMalay)
            "Nombor $numWord! Jom kita kira $itemWord!"
        else
            "Number $numWord! Let's count $itemWord!"
        SpeechEngine.speak(speech)
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
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            ToddlerAppHeader(
                title = LanguageManager.numbersTitle(),
                subtitle = LanguageManager.numbersSubtitle(),
                onBack = onBack,
                actionSlot = {
                    AppIconButton(
                        icon = Icons.Rounded.Refresh,
                        contentDescription = LanguageManager.again(),
                        size = 38.dp,
                        iconSize = 22.dp,
                        onClick = {
                            SoundEngine.playSparkle()
                            countedMap.clear()
                            celebrationPlayed = false
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Main Display Card (Big Number + Word + Arrows)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Button
                    AppIconButton(
                        icon = Icons.Rounded.ArrowBack,
                        contentDescription = "Previous Number",
                        size = 46.dp,
                        iconSize = 24.dp,
                        onClick = {
                            if (selectedIndex > 0) {
                                SoundEngine.playBoing()
                                selectedIndex--
                            }
                        }
                    )

                    // Big Number Badge & Spoken Word
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${currentItem.number}",
                            fontSize = 62.sp,
                            fontWeight = FontWeight.Black,
                            color = currentItem.color,
                            lineHeight = 62.sp
                        )
                        Text(
                            text = if (LanguageManager.isMalay()) currentItem.wordBm else currentItem.wordEn,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDark
                        )
                    }

                    // Next Button
                    AppIconButton(
                        icon = Icons.Rounded.ArrowForward,
                        contentDescription = "Next Number",
                        size = 46.dp,
                        iconSize = 24.dp,
                        onClick = {
                            if (selectedIndex < numbersList.size - 1) {
                                SoundEngine.playBoing()
                                selectedIndex++
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Instruction Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.92f))
                    .padding(vertical = 8.dp, horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                val countedCount = countedMap.count { it.value }
                Text(
                    text = if (LanguageManager.isMalay())
                        "👆 Sentuh setiap ${currentItem.itemNameBm} untuk mengira! ($countedCount / ${currentItem.number})"
                    else
                        "👆 Tap each ${currentItem.itemNameEn} to count! ($countedCount / ${currentItem.number})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Countable Items Grid / Flow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color.White.copy(alpha = 0.65f))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Responsive rows for 1-10 items
                val totalItems = currentItem.number
                val itemsPerRow = when {
                    totalItems <= 3 -> totalItems
                    totalItems <= 6 -> 3
                    totalItems <= 8 -> 4
                    else -> 5
                }
                val rowCount = (totalItems + itemsPerRow - 1) / itemsPerRow

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (r in 0 until rowCount) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val startIdx = r * itemsPerRow
                            val endIdx = minOf(startIdx + itemsPerRow, totalItems)
                            for (i in startIdx until endIdx) {
                                val isCounted = countedMap[i] == true

                                val itemScale by animateFloatAsState(
                                    targetValue = if (isCounted) 1.15f else 1.0f,
                                    animationSpec = spring(dampingRatio = 0.45f, stiffness = 450f)
                                )

                                Box(
                                    modifier = Modifier
                                        .size(if (totalItems <= 4) 74.dp else 58.dp)
                                        .scale(itemScale)
                                        .clip(CircleShape)
                                        .background(if (isCounted) currentItem.color.copy(alpha = 0.28f) else Color.White)
                                        .border(
                                            width = if (isCounted) 3.dp else 2.dp,
                                            color = if (isCounted) currentItem.color else Color(0xFFCBD5E1),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            if (!isCounted) {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                countedMap[i] = true
                                                SoundEngine.playPop()

                                                val currentCountedTotal = countedMap.count { it.value }
                                                val countWord = if (LanguageManager.isMalay()) {
                                                    malayNumberWords.getOrElse(currentCountedTotal - 1) { "$currentCountedTotal" }
                                                } else {
                                                    englishNumberWords.getOrElse(currentCountedTotal - 1) { "$currentCountedTotal" }
                                                }
                                                SpeechEngine.speak(countWord)

                                                if (currentCountedTotal == currentItem.number && !celebrationPlayed) {
                                                    celebrationPlayed = true
                                                    SoundEngine.playCelebrationFanfare()
                                                    confettiState.spawn(350f, 350f, 60)
                                                    val finalCheer = if (LanguageManager.isMalay())
                                                        "Hebat Airis! Ada ${currentItem.number} ${currentItem.itemNameBm}!"
                                                    else
                                                        "Awesome Airis! There are ${currentItem.number} ${currentItem.itemNameEn}!"
                                                    SpeechEngine.speak(finalCheer)
                                                }
                                            } else {
                                                SoundEngine.playSparkle()
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentItem.emoji,
                                        fontSize = if (totalItems <= 4) 38.sp else 30.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Number Picker Strip (1 to 10)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                itemsIndexed(numbersList) { idx, item ->
                    val isSelected = idx == selectedIndex
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) item.color else Color.White.copy(alpha = 0.85f))
                            .border(
                                width = if (isSelected) 2.5.dp else 1.5.dp,
                                color = if (isSelected) item.shadowColor else Color(0xFFCBD5E1),
                                shape = CircleShape
                            )
                            .clickable {
                                SoundEngine.playSparkle()
                                selectedIndex = idx
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${item.number}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) Color.White else TextDark
                        )
                    }
                }
            }
        }

        ConfettiOverlay(state = confettiState)
    }
}
