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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.R
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.core.LanguageManager
import com.pocketdev.airisgame.ui.components.ConfettiOverlay
import com.pocketdev.airisgame.ui.components.ModePillButton
import com.pocketdev.airisgame.ui.components.RealImageOrEmoji
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

data class AbcBilingualLetter(
    val letter: String,
    val lowercase: String,
    val enWord: String,
    val enEmoji: String,
    val bmWord: String,
    val bmEmoji: String,
    val cardColor: Color,
    val enImageRes: Int? = null,
    val bmImageRes: Int? = null
)

@Composable
fun AbcScreen(onBack: () -> Unit) {
    val confettiState = rememberConfettiState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val gridColumns = when {
        screenWidth >= 840 -> 4
        screenWidth >= 600 -> 3
        else -> 2
    }

    val alphabet = remember {
        listOf(
            AbcBilingualLetter("A", "a", "Apple", "🍎", "Ayam", "🐔", CandyCoral, enImageRes = R.drawable.thing_apple, bmImageRes = R.drawable.animal_chicken),
            AbcBilingualLetter("B", "b", "Ball", "⚽", "Bola", "⚽", WarmOrange, enImageRes = R.drawable.thing_ball, bmImageRes = R.drawable.thing_ball),
            AbcBilingualLetter("C", "c", "Cat", "🐱", "Cawan", "☕", SunnyYellow, enImageRes = R.drawable.animal_cat, bmImageRes = R.drawable.thing_cup),
            AbcBilingualLetter("D", "d", "Dog", "🐶", "Daun", "🍃", MintFresh, enImageRes = R.drawable.animal_dog, bmImageRes = R.drawable.thing_leaf),
            AbcBilingualLetter("E", "e", "Elephant", "🐘", "Epal", "🍎", SkyVibrant, enImageRes = R.drawable.animal_elephant, bmImageRes = R.drawable.thing_apple),
            AbcBilingualLetter("F", "f", "Fish", "🐟", "Feri", "⛴️", FreshTeal, enImageRes = R.drawable.animal_fish, bmImageRes = R.drawable.vehicle_boat),
            AbcBilingualLetter("G", "g", "Giraffe", "🦒", "Gajah", "🐘", SunnyYellow, enImageRes = R.drawable.animal_giraffe, bmImageRes = R.drawable.animal_elephant),
            AbcBilingualLetter("H", "h", "Horse", "🐴", "Harimau", "🐯", MagicLavender, enImageRes = R.drawable.animal_horse, bmImageRes = R.drawable.animal_tiger),
            AbcBilingualLetter("I", "i", "Ice Cream", "🍦", "Ikan", "🐟", BubblePink, enImageRes = R.drawable.thing_icecream, bmImageRes = R.drawable.animal_fish),
            AbcBilingualLetter("J", "j", "Juice", "🧃", "Jam", "⏰", CandyCoral, bmImageRes = R.drawable.thing_clock),
            AbcBilingualLetter("K", "k", "Kite", "🪁", "Kucing", "🐱", SkyVibrant, bmImageRes = R.drawable.animal_cat),
            AbcBilingualLetter("L", "l", "Lion", "🦁", "Lembu", "🐮", WarmOrange, enImageRes = R.drawable.animal_lion, bmImageRes = R.drawable.animal_cow),
            AbcBilingualLetter("M", "m", "Monkey", "🐵", "Monyet", "🐵", SunnyYellow, enImageRes = R.drawable.animal_ape, bmImageRes = R.drawable.animal_ape),
            AbcBilingualLetter("N", "n", "Nest", "🪹", "Nanas", "🍍", MintFresh, bmImageRes = R.drawable.thing_pineapple),
            AbcBilingualLetter("O", "o", "Owl", "🦉", "Oren", "🍊", MagicLavender, enImageRes = R.drawable.animal_owl, bmImageRes = R.drawable.thing_orange),
            AbcBilingualLetter("P", "p", "Penguin", "🐧", "Pisang", "🍌", BubblePink, enImageRes = R.drawable.animal_penguin, bmImageRes = R.drawable.thing_banana),
            AbcBilingualLetter("Q", "q", "Queen", "👑", "Quran", "📖", SunnyYellow),
            AbcBilingualLetter("R", "r", "Rabbit", "🐰", "Rusa", "🦌", CandyCoral),
            AbcBilingualLetter("S", "s", "Sun", "☀️", "Singa", "🦁", WarmOrange, bmImageRes = R.drawable.animal_lion),
            AbcBilingualLetter("T", "t", "Tiger", "🐯", "Topi", "🧢", SunnyYellow, enImageRes = R.drawable.animal_tiger, bmImageRes = R.drawable.thing_hat),
            AbcBilingualLetter("U", "u", "Umbrella", "☂️", "Udang", "🦐", SkyVibrant),
            AbcBilingualLetter("V", "v", "Van", "🚐", "Van", "🚐", FreshTeal, enImageRes = R.drawable.vehicle_van, bmImageRes = R.drawable.vehicle_van),
            AbcBilingualLetter("W", "w", "Whale", "🐋", "Wau", "🪁", SkyVibrant),
            AbcBilingualLetter("X", "x", "Xylophone", "🎹", "Xilofon", "🎹", MagicLavender),
            AbcBilingualLetter("Y", "y", "Yacht", "⛵", "Yo-yo", "🪀", FreshTeal, enImageRes = R.drawable.vehicle_boat),
            AbcBilingualLetter("Z", "z", "Zebra", "🦓", "Zirafah", "🦒", CandyCoral, enImageRes = R.drawable.animal_zebra, bmImageRes = R.drawable.animal_giraffe)
        )
    }

    var isQuizMode by remember { mutableStateOf(false) }
    var targetIndex by remember { mutableStateOf(0) }
    var promptMessage by remember { mutableStateOf("") }
    var showHintWiggle by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val wiggleAngle by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(220, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Quiz prompt repetition loop: repeats up to 5 times every ~5 seconds.
    // If not tapped within ~10 seconds (after 2 repetitions), smoothly wiggles the card as a hint!
    LaunchedEffect(isQuizMode, targetIndex, LanguageManager.currentLanguage) {
        showHintWiggle = false
        if (isQuizMode) {
            val target = alphabet[targetIndex]
            val word = if (LanguageManager.isMalay()) target.bmWord else target.enWord
            val emoji = if (LanguageManager.isMalay()) target.bmEmoji else target.enEmoji

            val promptText = if (LanguageManager.isMalay())
                "Di manakah huruf ${target.letter} untuk $word? $emoji"
            else
                "Can you find the letter ${target.letter} for $word? $emoji"

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
                "Sentuh mana-mana huruf untuk mendengar sebutan!"
            else
                "Tap any letter to hear the sound & see pictures!"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Bar with integrated Language Switcher and Sound Settings Button
            ToddlerAppHeader(
                title = LanguageManager.abcTitle(),
                subtitle = LanguageManager.abcSubtitle(),
                onBack = onBack,
                actionSlot = {
                    ModePillButton(
                        isActive = isQuizMode,
                        activeText = LanguageManager.finding(),
                        inactiveText = LanguageManager.findMe(),
                        onClick = {
                            isQuizMode = !isQuizMode
                            if (isQuizMode) {
                                targetIndex = Random.nextInt(alphabet.size)
                                SoundEngine.playSparkle()
                            } else {
                                SoundEngine.playBoing()
                            }
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Cheerful Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
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

            Spacer(modifier = Modifier.height(8.dp))

            // Alphabet Grid (Responsive Columns)
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(alphabet) { item ->
                    val isTarget = isQuizMode && item.letter == alphabet[targetIndex].letter
                    val shouldWiggle = isTarget && showHintWiggle
                    val isMalay = LanguageManager.isMalay()
                    val word = if (isMalay) item.bmWord else item.enWord
                    val emoji = if (isMalay) item.bmEmoji else item.enEmoji
                    val imgRes = if (isMalay) item.bmImageRes else item.enImageRes

                    AbcLetterCard(
                        item = item,
                        word = word,
                        emoji = emoji,
                        imageRes = imgRes,
                        wiggleAngle = if (shouldWiggle) wiggleAngle else 0f,
                        onClick = {
                            if (isQuizMode) {
                                val target = alphabet[targetIndex]
                                if (item.letter == target.letter) {
                                    SoundEngine.playCelebrationFanfare()
                                    SpeechEngine.speak(
                                        if (LanguageManager.isMalay())
                                            "Hebat! Huruf ${item.letter} untuk $word!"
                                        else
                                            "Great job! Letter ${item.letter} for $word!"
                                    )
                                    confettiState.spawn(320f, 380f, 40)
                                    targetIndex = (targetIndex + 1 + Random.nextInt(alphabet.size - 1)) % alphabet.size
                                } else {
                                    SoundEngine.playWobble()
                                    val targetWord = if (LanguageManager.isMalay()) target.bmWord else target.enWord
                                    SpeechEngine.speak(
                                        if (LanguageManager.isMalay())
                                            "Ini ${item.letter} untuk $word! Cuba cari ${target.letter}!"
                                        else
                                            "That's ${item.letter} for $word! Find ${target.letter}!"
                                    )
                                }
                            } else {
                                SoundEngine.playBoing()
                                SpeechEngine.speak(
                                    if (LanguageManager.isMalay())
                                        "${item.letter}... ${item.letter} untuk $word!"
                                    else
                                        "${item.letter}... ${item.letter} is for $word!"
                                )
                                confettiState.spawn(320f, 380f, 15)
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
private fun AbcLetterCard(
    item: AbcBilingualLetter,
    word: String,
    emoji: String,
    imageRes: Int?,
    wiggleAngle: Float,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 420f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .scale(scale)
            .rotate(wiggleAngle)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .semantics { contentDescription = "Letter ${item.letter}, $word" },
        contentAlignment = Alignment.Center
    ) {
        // Shadow base
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 5.dp)
                .background(CoralShadow.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
        )

        // Surface
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Large Letter Display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${item.letter}${item.lowercase}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = item.cardColor
                    )
                    Text(
                        text = word,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1
                    )
                }

                // Real Picture or Emoji Illustration
                RealImageOrEmoji(
                    imageRes = imageRes,
                    emoji = emoji,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    emojiSize = 44.sp,
                    cornerRadius = 14.dp,
                    contentDescription = word
                )
            }
        }
    }
}
