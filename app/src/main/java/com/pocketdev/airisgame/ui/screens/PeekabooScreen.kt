package com.pocketdev.airisgame.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalConfiguration
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.core.LanguageManager
import com.pocketdev.airisgame.ui.components.AppBackButton
import com.pocketdev.airisgame.ui.components.AppIconButton
import com.pocketdev.airisgame.ui.components.ConfettiOverlay
import com.pocketdev.airisgame.ui.components.ToddlerAppHeader
import com.pocketdev.airisgame.ui.components.rememberConfettiState
import com.pocketdev.airisgame.ui.theme.BubblePink
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.CoralShadow
import com.pocketdev.airisgame.ui.theme.MagicLavender
import com.pocketdev.airisgame.ui.theme.MintFresh
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark
import com.pocketdev.airisgame.ui.theme.WarmOrange
import kotlinx.coroutines.launch

data class SurpriseEgg(
    val color: Color,
    val patternEmoji: String,
    val surpriseEmoji: String,
    val enName: String,
    val bmName: String,
    var taps: Int = 0
)

@Composable
fun PeekabooScreen(onBack: () -> Unit) {
    val confettiState = rememberConfettiState()

    val eggs = remember {
        mutableStateListOf(
            SurpriseEgg(CandyCoral, "⭐", "🐣", "Baby Chick! Peep peep!", "Anak Ayam! Ciap ciap!"),
            SurpriseEgg(SkyVibrant, "🌊", "🐬", "Friendly Dolphin! Splash!", "Ikan Lumba-lumba!"),
            SurpriseEgg(MintFresh, "🍀", "🐸", "Green Froggy! Ribbit!", "Katak Hijau! Ong-ong!"),
            SurpriseEgg(SunnyYellow, "☀️", "🦁", "Baby Lion! Roar!", "Anak Singa! Roarrr!"),
            SurpriseEgg(MagicLavender, "✨", "🦄", "Sparkle Unicorn! Magic!", "Kuda Tanduk Ajaib!"),
            SurpriseEgg(BubblePink, "💖", "🐰", "Fluffy Bunny! Hop hop!", "Arnab Comel! Lompat!"),
            SurpriseEgg(WarmOrange, "🔥", "🦖", "Friendly Dino! Stomp!", "Dinosaur Gagah!"),
            SurpriseEgg(SkyVibrant, "🚀", "🐼", "Cuddly Panda! Peekaboo!", "Panda Comel! Cak-cak!")
        )
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
            // Universal Header with integrated Language Switcher and Sound Settings Button
            ToddlerAppHeader(
                title = LanguageManager.peekabooTitle(),
                subtitle = LanguageManager.peekabooSubtitle(),
                onBack = onBack,
                actionSlot = {
                    AppIconButton(
                        icon = Icons.Rounded.Refresh,
                        contentDescription = LanguageManager.again(),
                        size = 38.dp,
                        iconSize = 22.dp,
                        onClick = {
                            SoundEngine.playSparkle()
                            for (i in 0 until eggs.size) {
                                eggs[i] = eggs[i].copy(taps = 0)
                            }
                            SpeechEngine.speak(
                                if (LanguageManager.isMalay()) "Telur kejutan baharu sedia!" else "New surprise eggs ready!"
                            )
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
                    text = if (LanguageManager.isMalay())
                        "Sentuh telur 3 kali untuk retakkannya! 🐣"
                    else
                        "Tap any egg 3 times to crack it open! 🐣",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Eggs Grid (Responsive Columns)
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(eggs) { index, egg ->
                    val eggName = if (LanguageManager.isMalay()) egg.bmName else egg.enName

                    EggCard(
                        egg = egg,
                        displayName = eggName,
                        onClick = {
                            val nextTaps = egg.taps + 1
                            eggs[index] = egg.copy(taps = nextTaps)

                            when (nextTaps) {
                                1 -> {
                                    SoundEngine.playBoing()
                                    SpeechEngine.speak(if (LanguageManager.isMalay()) "Retak!" else "Crack!")
                                }
                                2 -> {
                                    SoundEngine.playPop()
                                    SpeechEngine.speak(if (LanguageManager.isMalay()) "Hampir terbuka!" else "Almost open!")
                                }
                                3 -> {
                                    SoundEngine.playCelebrationFanfare()
                                    SpeechEngine.speak(
                                        if (LanguageManager.isMalay()) "Cak-cak! Ini $eggName!" else "Peekaboo! It's a $eggName!"
                                    )
                                    confettiState.spawn(300f, 400f, 35)
                                }
                                else -> {
                                    SoundEngine.playSparkle()
                                    SpeechEngine.speak(eggName)
                                    confettiState.spawn(300f, 400f, 15)
                                }
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
private fun EggCard(
    egg: SurpriseEgg,
    displayName: String,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val wiggle = remember { Animatable(0f) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 450f)
    )

    val isHatched = egg.taps >= 3

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp)
            .scale(scale)
            .rotate(wiggle.value)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                // Smooth physical wiggle/shake on tap
                coroutineScope.launch {
                    wiggle.snapTo(0f)
                    wiggle.animateTo(
                        targetValue = 0f,
                        animationSpec = keyframes {
                            durationMillis = 380
                            -10f at 60 with FastOutSlowInEasing
                            10f at 140 with FastOutSlowInEasing
                            -6f at 220 with FastOutSlowInEasing
                            4f at 300 with FastOutSlowInEasing
                            0f at 380 with FastOutSlowInEasing
                        }
                    )
                }
                onClick()
            }
            .semantics {
                contentDescription = if (isHatched) displayName else "Surprise egg with ${egg.taps} taps"
            },
        contentAlignment = Alignment.Center
    ) {
        // Shadow base
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 5.dp)
                .background(CoralShadow.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
        )

        // Egg Surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(32.dp))
                .background(if (isHatched) Color.White else egg.color),
            contentAlignment = Alignment.Center
        ) {
            if (isHatched) {
                // Revealed Character
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = egg.surpriseEmoji,
                        fontSize = 58.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = displayName.split("!").first(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )
                }
            } else {
                // Intact or Cracking Egg
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val crackState = when (egg.taps) {
                        1 -> "⚡"
                        2 -> "💥"
                        else -> egg.patternEmoji
                    }
                    Text(
                        text = "🥚",
                        fontSize = 50.sp
                    )
                    Text(
                        text = crackState,
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}
