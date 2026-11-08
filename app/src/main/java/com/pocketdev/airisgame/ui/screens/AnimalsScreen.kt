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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.pocketdev.airisgame.ui.theme.CoralShadow
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.TextDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class AnimalBilingualInfo(
    val id: String,
    val enName: String,
    val bmName: String,
    val enSound: String,
    val bmSound: String,
    val emoji: String,
    val imageRes: Int,
    val soundResId: Int
)

@Composable
fun AnimalsScreen(onBack: () -> Unit) {
    val confettiState = rememberConfettiState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val gridColumns = when {
        screenWidth >= 840 -> 4
        screenWidth >= 600 -> 3
        else -> 2
    }

    val animals = remember {
        listOf(
            AnimalBilingualInfo("cat", "Cat", "Kucing", "Meow! Meow!", "Meow! Meow!", "🐱", R.drawable.animal_cat, R.raw.sound_animal_cat),
            AnimalBilingualInfo("dog", "Dog", "Anjing", "Woof! Woof!", "Gong! Gong!", "🐶", R.drawable.animal_dog, R.raw.sound_animal_dog),
            AnimalBilingualInfo("cow", "Cow", "Lembu", "Moo! Moo!", "Mooo!", "🐮", R.drawable.animal_cow, R.raw.sound_animal_cow),
            AnimalBilingualInfo("lion", "Lion", "Singa", "Roarrr!", "Roarrr!", "🦁", R.drawable.animal_lion, R.raw.sound_animal_lion),
            AnimalBilingualInfo("elephant", "Elephant", "Gajah", "Pawoo!", "Prowww!", "🐘", R.drawable.animal_elephant, R.raw.sound_animal_elephant),
            AnimalBilingualInfo("duck", "Duck", "Itik", "Quack! Quack!", "Kwek! Kwek!", "🦆", R.drawable.animal_duck, R.raw.sound_animal_duck),
            AnimalBilingualInfo("sheep", "Sheep", "Biri-biri", "Baa! Baa!", "Mbekk!", "🐑", R.drawable.animal_sheep, R.raw.sound_animal_sheep),
            AnimalBilingualInfo("horse", "Horse", "Kuda", "Neighhh!", "Hiii-ha!", "🐴", R.drawable.animal_horse, R.raw.sound_animal_horse),
            AnimalBilingualInfo("pig", "Pig", "Khinzir", "Oink! Oink!", "Oink!", "🐷", R.drawable.animal_pig, R.raw.sound_animal_pig),
            AnimalBilingualInfo("tiger", "Tiger", "Harimau", "Grrrr!", "Aumm!", "🐯", R.drawable.animal_tiger, R.raw.sound_animal_tiger),
            AnimalBilingualInfo("frog", "Frog", "Katak", "Ribbit! Croak!", "Ong-ong!", "🐸", R.drawable.animal_frog, R.raw.sound_animal_frog),
            AnimalBilingualInfo("rooster", "Rooster", "Ayam Jantan", "Cock-a-doodle-doo!", "Kok-kok-kok!", "🐓", R.drawable.animal_rooster, R.raw.sound_animal_rooster),
            AnimalBilingualInfo("owl", "Owl", "Burung Hantu", "Hoot! Hoot!", "Hoot!", "🦉", R.drawable.animal_owl, R.raw.sound_animal_owl),
            AnimalBilingualInfo("wolf", "Wolf", "Serigala", "Awooooo!", "Awoooo!", "🐺", R.drawable.animal_wolf, R.raw.sound_animal_wolf),
            AnimalBilingualInfo("zebra", "Zebra", "Kuda Belang", "Neigh!", "Neigh!", "🦓", R.drawable.animal_zebra, R.raw.sound_animal_zebra),
            AnimalBilingualInfo("bee", "Bee", "Lebah", "Bzzzzz!", "Dengung!", "🐝", R.drawable.animal_bee, R.raw.sound_animal_bee),
            AnimalBilingualInfo("chicken", "Chicken", "Ayam Betina", "Cluck! Cluck!", "Kot-kot!", "🐔", R.drawable.animal_chicken, R.raw.sound_animal_chicken),
            AnimalBilingualInfo("ape", "Monkey", "Monyet", "Ooh-ooh aah-aah!", "U-u a-a!", "🐵", R.drawable.animal_ape, R.raw.sound_animal_ape),
            AnimalBilingualInfo("penguin", "Penguin", "Penguin", "Honk! Squawk!", "Kwek!", "🐧", R.drawable.animal_penguin, R.raw.sound_animal_penguin),
            AnimalBilingualInfo("leopard", "Leopard", "Harimau Bintang", "Roarrr!", "Aummm!", "🐆", R.drawable.animal_leopard, R.raw.sound_animal_leopard)
        )
    }

    var isQuizMode by remember { mutableStateOf(false) }
    var targetAnimalIndex by remember { mutableStateOf(0) }
    var promptMessage by remember { mutableStateOf("") }
    var showHintWiggle by remember { mutableStateOf(false) }

    // Quiz 5-repetition loop with ~10s visual hint wiggle
    LaunchedEffect(isQuizMode, targetAnimalIndex, LanguageManager.currentLanguage) {
        showHintWiggle = false
        if (isQuizMode) {
            val target = animals[targetAnimalIndex]
            val name = if (LanguageManager.isMalay()) target.bmName else target.enName
            val prompt = if (LanguageManager.isMalay())
                "Di manakah $name? ${target.emoji}"
            else
                "Can you find the ${target.enName}? ${target.emoji}"

            promptMessage = prompt

            var count = 0
            while (count < 5 && isQuizMode) {
                SpeechEngine.speak(prompt)
                count++
                if (count >= 2) {
                    showHintWiggle = true
                }
                delay(5000L)
            }
        } else {
            promptMessage = if (LanguageManager.isMalay())
                "Sentuh mana-mana haiwan untuk dengar suara aslinya!"
            else
                "Tap any animal to hear its real sound & watch it wiggle!"
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
            // Universal Header with integrated Language Toggle & Audio Settings Button
            ToddlerAppHeader(
                title = LanguageManager.animalsTitle(),
                subtitle = LanguageManager.animalsSubtitle(),
                onBack = onBack,
                actionSlot = {
                    ModePillButton(
                        isActive = isQuizMode,
                        activeText = LanguageManager.finding(),
                        inactiveText = LanguageManager.findMe(),
                        onClick = {
                            isQuizMode = !isQuizMode
                            if (isQuizMode) {
                                targetAnimalIndex = Random.nextInt(animals.size)
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

            // Animals Grid (Responsive 2 to 4 Columns)
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(animals) { index, animal ->
                    val isTarget = isQuizMode && index == targetAnimalIndex
                    val name = if (LanguageManager.isMalay()) animal.bmName else animal.enName
                    val sound = if (LanguageManager.isMalay()) animal.bmSound else animal.enSound

                    AnimalCard(
                        animal = animal,
                        displayName = name,
                        soundText = sound,
                        shouldHintWiggle = isTarget && showHintWiggle,
                        onClick = {
                            if (isQuizMode) {
                                if (isTarget) {
                                    SoundEngine.playCelebrationFanfare()
                                    SpeechEngine.speak(
                                        if (LanguageManager.isMalay()) "Tahniah! Ini $name!" else "Great job! That's the $name!"
                                    )
                                    confettiState.spawn(300f, 350f, 40)
                                    targetAnimalIndex = (targetAnimalIndex + 1 + Random.nextInt(animals.size - 1)) % animals.size
                                } else {
                                    // Play the tapped animal's sound
                                    SoundEngine.playRawSound(animal.soundResId)
                                    val target = animals[targetAnimalIndex]
                                    val targetName = if (LanguageManager.isMalay()) target.bmName else target.enName
                                    SpeechEngine.speak(
                                        if (LanguageManager.isMalay()) "Ini $name! Cari $targetName!" else "That's $name! Find $targetName!"
                                    )
                                }
                            } else {
                                // Play authentic real animal sound
                                SoundEngine.playRawSound(animal.soundResId)
                                SpeechEngine.speak(name)
                                confettiState.spawn(200f + (index % gridColumns) * 80f, 400f, 15)
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
private fun AnimalCard(
    animal: AnimalBilingualInfo,
    displayName: String,
    soundText: String,
    shouldHintWiggle: Boolean,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val wiggle = remember { Animatable(0f) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f)
    )

    // Hint wiggle in quiz mode
    LaunchedEffect(shouldHintWiggle) {
        if (shouldHintWiggle) {
            while (true) {
                wiggle.animateTo(
                    targetValue = 0f,
                    animationSpec = keyframes {
                        durationMillis = 500
                        -8f at 80 with FastOutSlowInEasing
                        8f at 160 with FastOutSlowInEasing
                        -6f at 250 with FastOutSlowInEasing
                        4f at 340 with FastOutSlowInEasing
                        0f at 500 with FastOutSlowInEasing
                    }
                )
                delay(600L)
            }
        } else {
            wiggle.snapTo(0f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .scale(scale)
            .rotate(wiggle.value)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                coroutineScope.launch {
                    wiggle.snapTo(0f)
                    wiggle.animateTo(
                        targetValue = 0f,
                        animationSpec = keyframes {
                            durationMillis = 400
                            -12f at 70 with FastOutSlowInEasing
                            12f at 150 with FastOutSlowInEasing
                            -8f at 230 with FastOutSlowInEasing
                            6f at 310 with FastOutSlowInEasing
                            0f at 400 with FastOutSlowInEasing
                        }
                    )
                }
                onClick()
            }
            .semantics { contentDescription = "$displayName, says $soundText" },
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Real Animal Photo with Rounded Corners
                RealImageOrEmoji(
                    imageRes = animal.imageRes,
                    emoji = animal.emoji,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(90.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    emojiSize = 52.sp,
                    cornerRadius = 16.dp,
                    contentDescription = displayName
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = displayName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark,
                    maxLines = 1
                )

                Text(
                    text = soundText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}
