package com.pocketdev.airisgame.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.core.LanguageManager
import androidx.compose.foundation.border
import com.pocketdev.airisgame.ui.components.AppBackButton
import com.pocketdev.airisgame.ui.components.ConfettiOverlay
import com.pocketdev.airisgame.ui.components.ToddlerAppHeader
import com.pocketdev.airisgame.ui.components.rememberConfettiState
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.MagicLavender
import com.pocketdev.airisgame.ui.theme.MintFresh
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark
import com.pocketdev.airisgame.ui.theme.WarmOrange
import kotlinx.coroutines.launch
import kotlin.random.Random

data class ShapeBilingualItem(
    val enName: String,
    val bmName: String,
    val symbol: String,
    val color: Color
)

@Composable
fun ShapesScreen(onBack: () -> Unit) {
    val confettiState = rememberConfettiState()

    val allShapes = remember {
        listOf(
            ShapeBilingualItem("Star", "Bintang", "⭐", SunnyYellow),
            ShapeBilingualItem("Heart", "Hati", "❤️", CandyCoral),
            ShapeBilingualItem("Circle", "Bulat", "🟡", WarmOrange),
            ShapeBilingualItem("Triangle", "Segi Tiga", "🔺", SkyVibrant),
            ShapeBilingualItem("Square", "Segi Empat", "🟩", MintFresh),
            ShapeBilingualItem("Diamond", "Berlian", "🔷", MagicLavender)
        )
    }

    var currentIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var currentOptions by remember { mutableStateOf(listOf<ShapeBilingualItem>()) }

    fun refreshOptions(target: ShapeBilingualItem) {
        val others = allShapes.filter { it.enName != target.enName }.shuffled().take(2)
        currentOptions = (others + target).shuffled()
    }

    LaunchedEffect(currentIndex, LanguageManager.currentLanguage) {
        val target = allShapes[currentIndex]
        refreshOptions(target)
        val name = if (LanguageManager.isMalay()) target.bmName else target.enName
        val prompt = if (LanguageManager.isMalay()) "Cari bentuk $name!" else "Find the $name!"
        SpeechEngine.speak(prompt)
    }

    val currentTarget = allShapes[currentIndex]
    val targetDisplayName = if (LanguageManager.isMalay()) currentTarget.bmName else currentTarget.enName

    val infiniteTransition = rememberInfiniteTransition()
    val targetGlow by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Universal Header with integrated Language Switcher and Sound Settings Button
            ToddlerAppHeader(
                title = LanguageManager.shapesTitle(),
                subtitle = LanguageManager.shapesSubtitle(),
                onBack = onBack,
                actionSlot = {
                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(22.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = SunnyYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$score",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextDark
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Big Target Silhouette Card
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .scale(targetGlow)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color.White.copy(alpha = 0.92f))
                    .clickable {
                        SoundEngine.playBoing()
                        SpeechEngine.speak(
                            if (LanguageManager.isMalay()) "Cari bentuk $targetDisplayName!" else "Find the $targetDisplayName!"
                        )
                    }
                    .semantics { contentDescription = "Target is $targetDisplayName" },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currentTarget.symbol,
                        fontSize = 86.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = targetDisplayName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (LanguageManager.isMalay()) "Sentuh bentuk yang sepadan di bawah!" else "Tap the matching shape below!",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3 Big Choice Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                currentOptions.forEach { option ->
                    val optName = if (LanguageManager.isMalay()) option.bmName else option.enName

                    ShapeOptionTile(
                        item = option,
                        displayName = optName,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (option.enName == currentTarget.enName) {
                                // Correct!
                                score++
                                SoundEngine.playCelebrationFanfare()
                                SpeechEngine.speak(
                                    if (LanguageManager.isMalay()) "Hebat! Itu $optName!" else "Hooray! That's the $optName!"
                                )
                                confettiState.spawn(350f, 400f, 40)
                                currentIndex = (currentIndex + 1 + Random.nextInt(allShapes.size - 1)) % allShapes.size
                            } else {
                                // Friendly retry
                                SoundEngine.playWobble()
                                SpeechEngine.speak(
                                    if (LanguageManager.isMalay()) "Itu $optName! Jom cari $targetDisplayName!" else "That's a $optName! Let's find $targetDisplayName!"
                                )
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
private fun ShapeOptionTile(
    item: ShapeBilingualItem,
    displayName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val wiggle = remember { Animatable(0f) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f)
    )

    Box(
        modifier = modifier
            .height(130.dp)
            .scale(scale)
            .rotate(wiggle.value)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                // Smooth physical wiggle on tap
                coroutineScope.launch {
                    wiggle.snapTo(0f)
                    wiggle.animateTo(
                        targetValue = 0f,
                        animationSpec = keyframes {
                            durationMillis = 400
                            -10f at 70 with FastOutSlowInEasing
                            10f at 150 with FastOutSlowInEasing
                            -6f at 230 with FastOutSlowInEasing
                            4f at 310 with FastOutSlowInEasing
                            0f at 400 with FastOutSlowInEasing
                        }
                    )
                }
                onClick()
            }
            .semantics { contentDescription = "Select $displayName" },
        contentAlignment = Alignment.Center
    ) {
        // Shadow base
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 5.dp)
                .background(item.color.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
        )

        // Card Surface
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.symbol,
                    fontSize = 50.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = displayName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
        }
    }
}
