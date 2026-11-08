package com.pocketdev.airisgame.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.core.LanguageManager
import androidx.compose.ui.platform.LocalConfiguration
import com.pocketdev.airisgame.ui.components.AppBackButton
import com.pocketdev.airisgame.ui.components.ConfettiOverlay
import com.pocketdev.airisgame.ui.components.ModePillButton
import com.pocketdev.airisgame.ui.components.ToddlerAppHeader
import com.pocketdev.airisgame.ui.components.rememberConfettiState
import com.pocketdev.airisgame.ui.theme.BubblePink
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.CoralShadow
import com.pocketdev.airisgame.ui.theme.FreshTeal
import com.pocketdev.airisgame.ui.theme.MagicLavender
import com.pocketdev.airisgame.ui.theme.MintFresh
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark
import com.pocketdev.airisgame.ui.theme.WarmOrange
import kotlinx.coroutines.delay
import kotlin.random.Random

data class HijaiyahVowelLetter(
    val rootArabic: String,
    val name: String,
    val color: Color,
    val fathahArabic: String,
    val fathahSound: String,
    val kasrahArabic: String,
    val kasrahSound: String,
    val dammahArabic: String,
    val dammahSound: String
)

@Composable
fun HijaiyahScreen(onBack: () -> Unit) {
    val confettiState = rememberConfettiState()

    val letters = remember {
        listOf(
            HijaiyahVowelLetter("ا", "Alif", CandyCoral, "اَ", "Aa", "اِ", "Ee", "اُ", "Oo"),
            HijaiyahVowelLetter("ب", "Baa", WarmOrange, "بَ", "Ba", "بِ", "Bi", "بُ", "Bu"),
            HijaiyahVowelLetter("ت", "Taa", SunnyYellow, "تَ", "Ta", "تِ", "Ti", "تُ", "Tu"),
            HijaiyahVowelLetter("ث", "Thaa", MintFresh, "ثَ", "Tha", "ثِ", "Thi", "ثُ", "Thu"),
            HijaiyahVowelLetter("ج", "Jeem", SkyVibrant, "جَ", "Ja", "جِ", "Ji", "جُ", "Ju"),
            HijaiyahVowelLetter("ح", "Haa", FreshTeal, "حَ", "Ha", "حِ", "Hi", "حُ", "Hu"),
            HijaiyahVowelLetter("خ", "Khaa", WarmOrange, "خَ", "Kha", "خِ", "Khi", "خُ", "Khu"),
            HijaiyahVowelLetter("د", "Daal", MagicLavender, "دَ", "Da", "دِ", "Di", "دُ", "Du"),
            HijaiyahVowelLetter("ذ", "Dhaal", BubblePink, "ذَ", "Dha", "ذِ", "Dhi", "ذُ", "Dhu"),
            HijaiyahVowelLetter("ر", "Raa", CandyCoral, "رَ", "Ra", "رِ", "Ri", "رُ", "Ru"),
            HijaiyahVowelLetter("ز", "Zaay", SunnyYellow, "زَ", "Za", "زِ", "Zi", "زُ", "Zu"),
            HijaiyahVowelLetter("س", "Seen", MintFresh, "سَ", "Sa", "سِ", "Si", "سُ", "Su"),
            HijaiyahVowelLetter("ش", "Sheen", WarmOrange, "شَ", "Sha", "شِ", "Shi", "شُ", "Shu"),
            HijaiyahVowelLetter("ص", "Saad", SkyVibrant, "صَ", "So", "صِ", "Si", "صُ", "Su"),
            HijaiyahVowelLetter("ض", "Daad", FreshTeal, "ضَ", "Do", "ضِ", "Di", "ضُ", "Du"),
            HijaiyahVowelLetter("ط", "Taa", SunnyYellow, "طَ", "To", "طِ", "Ti", "طُ", "Tu"),
            HijaiyahVowelLetter("ظ", "Zhaa", MagicLavender, "ظَ", "Zho", "ظِ", "Zhi", "ظُ", "Zhu"),
            HijaiyahVowelLetter("ع", "Ayn", BubblePink, "عَ", "'A", "عِ", "'I", "عُ", "'U"),
            HijaiyahVowelLetter("غ", "Ghayn", MintFresh, "غَ", "Gha", "غِ", "Ghi", "غُ", "Ghu"),
            HijaiyahVowelLetter("ف", "Faa", CandyCoral, "فَ", "Fa", "فِ", "Fi", "فُ", "Fu"),
            HijaiyahVowelLetter("ق", "Qaaf", SkyVibrant, "قَ", "Qo", "قِ", "Qi", "قُ", "Qu"),
            HijaiyahVowelLetter("ك", "Kaaf", WarmOrange, "كَ", "Ka", "كِ", "Ki", "كُ", "Ku"),
            HijaiyahVowelLetter("ل", "Laam", SunnyYellow, "لَ", "La", "لِ", "Li", "لُ", "Lu"),
            HijaiyahVowelLetter("م", "Meem", FreshTeal, "مَ", "Ma", "مِ", "Mi", "مُ", "Mu"),
            HijaiyahVowelLetter("ن", "Noon", MagicLavender, "نَ", "Na", "نِ", "Ni", "نُ", "Nu"),
            HijaiyahVowelLetter("و", "Waw", CandyCoral, "وَ", "Wa", "وِ", "Wi", "وُ", "Wu"),
            HijaiyahVowelLetter("هـ", "Haa", BubblePink, "هَ", "Ha", "هِ", "Hi", "هُ", "Hu"),
            HijaiyahVowelLetter("ي", "Yaa", SkyVibrant, "يَ", "Ya", "يِ", "Yi", "يُ", "Yu")
        )
    }

    var isQuizMode by remember { mutableStateOf(false) }
    var targetIndex by remember { mutableStateOf(0) }
    var promptMessage by remember { mutableStateOf("") }
    var showHintWiggle by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val wiggleAngle by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(220, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Quiz repetition loop (asks up to 5 times every ~5 seconds, wiggles hint if not answered)
    LaunchedEffect(isQuizMode, targetIndex) {
        showHintWiggle = false
        if (isQuizMode) {
            val target = letters[targetIndex]
            val promptText = if (LanguageManager.isMalay())
                "Di manakah huruf ${target.name} ${target.rootArabic}?"
            else
                "Can you find the letter ${target.name} ${target.rootArabic}?"

            promptMessage = promptText

            var count = 0
            while (count < 5 && isQuizMode) {
                SpeechEngine.speak(promptText)
                count++
                if (count >= 2) {
                    showHintWiggle = true
                }
                delay(5000L)
            }
        } else {
            promptMessage = if (LanguageManager.isMalay())
                "Sentuh bahagian atas untuk nama huruf, bahagian bawah untuk baris (a, i, u)!"
            else
                "Tap top for letter name, tap bottom 3 segments for vowels (a, i, u)!"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyGradient)
    ) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val gridColumns = when {
            screenWidth >= 840 -> 4
            screenWidth >= 600 -> 3
            else -> 2
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Header Bar with integrated Language Switcher and Sound Settings Button
            ToddlerAppHeader(
                title = "ا ب ت Hijaiyah",
                subtitle = if (LanguageManager.isMalay()) "Huruf & Baris (a, i, u)" else "Letters & Harakat (a, i, u)",
                onBack = onBack,
                actionSlot = {
                    ModePillButton(
                        isActive = isQuizMode,
                        activeText = LanguageManager.finding(),
                        inactiveText = LanguageManager.findMe(),
                        onClick = {
                            isQuizMode = !isQuizMode
                            if (isQuizMode) {
                                targetIndex = Random.nextInt(letters.size)
                                SoundEngine.playSparkle()
                            } else {
                                SoundEngine.playBoing()
                            }
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Cheerful Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.94f))
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = promptMessage,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hijaiyah Cards Grid (Responsive Columns, each tile has 4 distinct categories)
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(letters) { item ->
                    val isTarget = isQuizMode && item.name == letters[targetIndex].name
                    val shouldWiggle = isTarget && showHintWiggle

                    HijaiyahTile(
                        item = item,
                        wiggleAngle = if (shouldWiggle) wiggleAngle else 0f,
                        onRootClick = {
                            if (isQuizMode) {
                                val target = letters[targetIndex]
                                if (item.name == target.name) {
                                    SoundEngine.playCelebrationFanfare()
                                    SpeechEngine.speak(
                                        if (LanguageManager.isMalay()) "Hebat! Huruf ${item.name}!" else "Great job! Letter ${item.name}!"
                                    )
                                    confettiState.spawn(320f, 380f, 40)
                                    targetIndex = (targetIndex + 1 + Random.nextInt(letters.size - 1)) % letters.size
                                } else {
                                    SoundEngine.playWobble()
                                    SpeechEngine.speak(
                                        if (LanguageManager.isMalay()) "Ini huruf ${item.name}! Cuba cari ${target.name}!" else "That's ${item.name}! Find ${target.name}!"
                                    )
                                }
                            } else {
                                SoundEngine.playBoing()
                                SpeechEngine.speakArabic(item.rootArabic, item.name)
                                confettiState.spawn(320f, 380f, 15)
                            }
                        },
                        onFathahClick = {
                            SoundEngine.playSparkle()
                            SpeechEngine.speakArabic(item.fathahArabic, item.fathahSound)
                        },
                        onKasrahClick = {
                            SoundEngine.playSparkle()
                            SpeechEngine.speakArabic(item.kasrahArabic, item.kasrahSound)
                        },
                        onDammahClick = {
                            SoundEngine.playSparkle()
                            SpeechEngine.speakArabic(item.dammahArabic, item.dammahSound)
                        }
                    )
                }
            }
        }

        ConfettiOverlay(state = confettiState)
    }
}

/**
 * Single Hijaiyah Tile with 4 Categories:
 * - Upper Half: Root letter display and pronunciation (e.g. Alif, Baa, Thaa)
 * - Lower Half: 3 Segments for Fathah (a), Kasrah (i), Dammah (u)
 */
@Composable
private fun HijaiyahTile(
    item: HijaiyahVowelLetter,
    wiggleAngle: Float,
    onRootClick: () -> Unit,
    onFathahClick: () -> Unit,
    onKasrahClick: () -> Unit,
    onDammahClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(195.dp)
            .rotate(wiggleAngle)
            .semantics { contentDescription = "Letter ${item.name}" },
        contentAlignment = Alignment.Center
    ) {
        // Shadow base
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 5.dp)
                .background(CoralShadow.copy(alpha = 0.25f), RoundedCornerShape(26.dp))
        )

        // Card Surface
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // UPPER HALF: Root Arabic Letter (e.g. Alif, Baa, Thaa)
                val topInteractionSource = remember { MutableInteractionSource() }
                val isTopPressed by topInteractionSource.collectIsPressedAsState()
                val topScale by animateFloatAsState(
                    targetValue = if (isTopPressed) 1.08f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = 450f)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.25f)
                        .scale(topScale)
                        .clickable(
                            interactionSource = topInteractionSource,
                            indication = null
                        ) {
                            onRootClick()
                        }
                        .padding(top = 8.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = item.rootArabic,
                            fontSize = 46.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = item.color
                        )
                        Text(
                            text = item.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }
                }

                // Divider line separating upper category and lower categories
                Divider(
                    color = Color(0xFFF1F5F9),
                    thickness = 1.5.dp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // LOWER HALF: 3 Segments for Fathah (a), Kasrah (i), Dammah (u)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.95f)
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Segment 1: Fathah (-a)
                    VowelSegmentPill(
                        arabic = item.fathahArabic,
                        soundLabel = item.fathahSound,
                        containerColor = CandyCoral.copy(alpha = 0.15f),
                        textColor = CandyCoral,
                        modifier = Modifier.weight(1f),
                        onClick = onFathahClick
                    )

                    // Segment 2: Kasrah (-i)
                    VowelSegmentPill(
                        arabic = item.kasrahArabic,
                        soundLabel = item.kasrahSound,
                        containerColor = SkyVibrant.copy(alpha = 0.15f),
                        textColor = SkyVibrant,
                        modifier = Modifier.weight(1f),
                        onClick = onKasrahClick
                    )

                    // Segment 3: Dammah (-u)
                    VowelSegmentPill(
                        arabic = item.dammahArabic,
                        soundLabel = item.dammahSound,
                        containerColor = SunnyYellow.copy(alpha = 0.25f),
                        textColor = WarmOrange,
                        modifier = Modifier.weight(1f),
                        onClick = onDammahClick
                    )
                }
            }
        }
    }
}

@Composable
private fun VowelSegmentPill(
    arabic: String,
    soundLabel: String,
    containerColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 500f)
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .semantics { contentDescription = "$arabic $soundLabel" },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = arabic,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
            Text(
                text = soundLabel,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark.copy(alpha = 0.8f)
            )
        }
    }
}
