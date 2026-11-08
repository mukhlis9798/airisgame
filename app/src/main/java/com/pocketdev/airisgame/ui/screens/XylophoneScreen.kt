package com.pocketdev.airisgame.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.core.LanguageManager
import com.pocketdev.airisgame.ui.components.AppBackButton
import com.pocketdev.airisgame.ui.components.ConfettiOverlay
import com.pocketdev.airisgame.ui.components.ModePillButton
import com.pocketdev.airisgame.ui.components.ToddlerAppHeader
import com.pocketdev.airisgame.ui.components.rememberConfettiState
import com.pocketdev.airisgame.ui.theme.BubblePink
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.DeepIndigo
import com.pocketdev.airisgame.ui.theme.FreshTeal
import com.pocketdev.airisgame.ui.theme.MagicLavender
import com.pocketdev.airisgame.ui.theme.MintFresh
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark
import com.pocketdev.airisgame.ui.theme.TextLight
import com.pocketdev.airisgame.ui.theme.WarmOrange

data class XyloSong(
    val id: String,
    val enName: String,
    val bmName: String,
    val notes: List<Int>
)

data class XyloKey(
    val note: String,
    val solfege: String,
    val frequency: Float,
    val color: Color,
    val icon: String
)

@Composable
fun XylophoneScreen(onBack: () -> Unit) {
    val confettiState = rememberConfettiState()

    val keys = remember {
        listOf(
            XyloKey("C", "Do", 261.63f, CandyCoral, "🐱"),
            XyloKey("D", "Re", 293.66f, WarmOrange, "🐶"),
            XyloKey("E", "Mi", 329.63f, SunnyYellow, "🐥"),
            XyloKey("F", "Fa", 349.23f, MintFresh, "🐸"),
            XyloKey("G", "Sol", 392.00f, FreshTeal, "🐬"),
            XyloKey("A", "La", 440.00f, SkyVibrant, "🐘"),
            XyloKey("B", "Ti", 493.88f, DeepIndigo, "🦄"),
            XyloKey("C2", "Do", 523.25f, BubblePink, "🌈")
        )
    }

    val songs = remember {
        listOf(
            XyloSong(
                "twinkle",
                "Twinkle Twinkle Little Star",
                "Bintang Kecil Berkelipan",
                listOf(0, 0, 4, 4, 5, 5, 4, 3, 3, 2, 2, 1, 1, 0)
            ),
            XyloSong(
                "birthday",
                "Happy Birthday Song",
                "Selamat Hari Jadi",
                listOf(0, 0, 1, 0, 3, 2, 0, 0, 1, 0, 4, 3)
            ),
            XyloSong(
                "bus",
                "The Wheels on the Bus",
                "Roda Bas Berputar",
                listOf(0, 3, 3, 3, 3, 5, 7, 5, 3, 4)
            ),
            XyloSong(
                "babyshark",
                "Baby Shark (Doo Doo)",
                "Anak Jerung (Baby Shark)",
                listOf(1, 2, 4, 4, 4, 4, 4, 1, 2, 4, 4, 4, 4, 4)
            ),
            XyloSong(
                "mary",
                "Mary Had a Little Lamb",
                "Anak Kambing Mary",
                listOf(2, 1, 0, 1, 2, 2, 2, 1, 1, 1, 2, 4, 4)
            )
        )
    }

    var selectedSongIndex by remember { mutableStateOf(0) }
    var isSongMode by remember { mutableStateOf(false) }
    var songStep by remember { mutableStateOf(0) }
    var isSongDropdownOpen by remember { mutableStateOf(false) }

    val currentSong = songs[selectedSongIndex]
    val currentSongName = if (LanguageManager.isMalay()) currentSong.bmName else currentSong.enName

    LaunchedEffect(isSongMode, selectedSongIndex) {
        if (isSongMode) {
            songStep = 0
            val prompt = if (LanguageManager.isMalay())
                "Jom main lagu $currentSongName! Ikut kekunci yang membesar!"
            else
                "Let's play $currentSongName! Follow the glowing key!"
            SpeechEngine.speak(prompt)
        }
    }

    // Bouncing animation for guide indicator above target key
    val infiniteTransition = rememberInfiniteTransition()
    val starBounce by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

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
                title = LanguageManager.xylophoneTitle(),
                subtitle = LanguageManager.xylophoneSubtitle(),
                onBack = onBack,
                actionSlot = {
                    ModePillButton(
                        isActive = isSongMode,
                        activeText = LanguageManager.songGuide(),
                        inactiveText = LanguageManager.songGuide(),
                        activeIcon = Icons.Rounded.Star,
                        inactiveIcon = Icons.Rounded.PlayArrow,
                        onClick = {
                            isSongMode = !isSongMode
                            if (isSongMode) {
                                SoundEngine.playSparkle()
                            } else {
                                SoundEngine.playBoing()
                            }
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Song Selector Dropdown Card (Visible when Song Guide is active)
            if (isSongMode) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.White.copy(alpha = 0.95f))
                            .clickable {
                                SoundEngine.playSparkle()
                                isSongDropdownOpen = true
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .semantics { contentDescription = "Select song: $currentSongName" }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎵", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = currentSongName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextDark
                                    )
                                    Text(
                                        text = if (LanguageManager.isMalay()) "Sentuh untuk tukar lagu" else "Tap to change song",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = null,
                                tint = TextDark,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Dropdown Menu for Songs
                    DropdownMenu(
                        expanded = isSongDropdownOpen,
                        onDismissRequest = { isSongDropdownOpen = false },
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(18.dp))
                            .padding(4.dp)
                    ) {
                        songs.forEachIndexed { index, song ->
                            val sName = if (LanguageManager.isMalay()) song.bmName else song.enName
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "🎶 $sName",
                                        fontWeight = if (index == selectedSongIndex) FontWeight.Bold else FontWeight.Normal,
                                        color = if (index == selectedSongIndex) SkyVibrant else TextDark
                                    )
                                },
                                onClick = {
                                    selectedSongIndex = index
                                    songStep = 0
                                    isSongDropdownOpen = false
                                    SoundEngine.playSparkle()
                                    SpeechEngine.speak(sName)
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Xylophone Bars Container
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.88f))
                    .padding(horizontal = 10.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                keys.forEachIndexed { index, key ->
                    val isTarget = isSongMode && (currentSong.notes[songStep % currentSong.notes.size] == index)
                    // In song mode, non-target keys become lowered in transparency (dimmed to 0.38f)
                    val barAlpha = if (!isSongMode || isTarget) 1.0f else 0.38f
                    // The keys graduate in height from longest (lowest note) to shortest (highest note)
                    val heightRatio = 1.0f - (index * 0.045f)

                    XylophoneBar(
                        key = key,
                        isTarget = isTarget,
                        alpha = barAlpha,
                        heightRatio = heightRatio,
                        starBounceY = starBounce,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 3.dp),
                        onClick = {
                            if (isSongMode) {
                                val currentTargetIndex = currentSong.notes[songStep % currentSong.notes.size]
                                if (index == currentTargetIndex) {
                                    SoundEngine.playXylophoneNote(key.frequency)
                                    songStep++
                                    confettiState.spawn(100f + index * 60f, 400f, 15)
                                    if (songStep % currentSong.notes.size == 0) {
                                        SoundEngine.playCelebrationFanfare()
                                        SpeechEngine.speak(
                                            if (LanguageManager.isMalay())
                                                "Hebat! Anda berjaya memainkan lagu $currentSongName!"
                                            else
                                                "Hooray! You played $currentSongName!"
                                        )
                                        confettiState.spawn(300f, 300f, 50)
                                    }
                                } else {
                                    // Soft gentle feedback on non-target key in guide mode
                                    SoundEngine.playWobble()
                                }
                            } else {
                                SoundEngine.playXylophoneNote(key.frequency)
                                confettiState.spawn(100f + index * 60f, 450f, 10)
                            }
                        }
                    )
                }
            }
        }

        ConfettiOverlay(state = confettiState)
    }
}

@Composable
private fun XylophoneBar(
    key: XyloKey,
    isTarget: Boolean,
    alpha: Float,
    heightRatio: Float,
    starBounceY: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // When target in guide mode, scale up bigger (1.14f) so toddler easily spots it
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else if (isTarget) 1.14f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 450f)
    )

    Box(
        modifier = modifier
            .fillMaxHeight(heightRatio)
            .scale(scale)
            .alpha(alpha)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .semantics { contentDescription = "Xylophone key ${key.solfege}, note ${key.note}" },
        contentAlignment = Alignment.Center
    ) {
        // Wooden frame shadow behind bar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 4.dp)
                .background(Color(0x33000000), RoundedCornerShape(16.dp))
        )

        // Vibrant metallic colored bar with glowing golden border on target
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(key.color)
                .then(
                    if (isTarget) Modifier.border(BorderStroke(3.dp, SunnyYellow), RoundedCornerShape(16.dp))
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Mount Peg or Bouncing Star for target
                if (isTarget) {
                    Box(
                        modifier = Modifier
                            .offset(y = starBounceY.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(SunnyYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⭐", fontSize = 13.sp)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f))
                    )
                }

                // Key Information
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = key.icon,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = key.solfege,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextLight
                    )
                    Text(
                        text = key.note,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight.copy(alpha = 0.8f)
                    )
                }

                // Bottom Mount Peg
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                )
            }
        }
    }
}
