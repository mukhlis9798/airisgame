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

data class VehicleBilingualInfo(
    val id: String,
    val enName: String,
    val bmName: String,
    val enSound: String,
    val bmSound: String,
    val emoji: String,
    val imageRes: Int,
    val rawSoundId: Int? = null,
    val soundAction: (() -> Unit)? = null
)

@Composable
fun TransportationScreen(onBack: () -> Unit) {
    val confettiState = rememberConfettiState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val gridColumns = when {
        screenWidth >= 840 -> 4
        screenWidth >= 600 -> 3
        else -> 2
    }

    val vehicles = remember {
        listOf(
            VehicleBilingualInfo(
                id = "car",
                enName = "Car",
                bmName = "Kereta",
                enSound = "Honk! Honk!",
                bmSound = "Hon! Pon!",
                emoji = "🚗",
                imageRes = R.drawable.vehicle_car,
                rawSoundId = R.raw.sound_vehicle_car
            ),
            VehicleBilingualInfo(
                id = "train",
                enName = "Train",
                bmName = "Kereta Api",
                enSound = "Choo-choo!",
                bmSound = "Choo-choo!",
                emoji = "🚂",
                imageRes = R.drawable.vehicle_train,
                rawSoundId = R.raw.sound_vehicle_train
            ),
            VehicleBilingualInfo(
                id = "airplane",
                enName = "Airplane",
                bmName = "Kapal Terbang",
                enSound = "Whooosh!",
                bmSound = "Fyuuush!",
                emoji = "✈️",
                imageRes = R.drawable.vehicle_airplane,
                soundAction = { SoundEngine.playAirplaneSound() }
            ),
            VehicleBilingualInfo(
                id = "firetruck",
                enName = "Fire Truck",
                bmName = "Lori Bomba",
                enSound = "Wee-woo-wee-woo!",
                bmSound = "Wiu-wiu-wiu!",
                emoji = "🚒",
                imageRes = R.drawable.vehicle_firetruck,
                soundAction = { SoundEngine.playFireTruckSiren() }
            ),
            VehicleBilingualInfo(
                id = "police",
                enName = "Police Car",
                bmName = "Kereta Polis",
                enSound = "Siren Yelp!",
                bmSound = "Wiu-wiu!",
                emoji = "🚓",
                imageRes = R.drawable.vehicle_police,
                rawSoundId = R.raw.sound_vehicle_police
            ),
            VehicleBilingualInfo(
                id = "ambulance",
                enName = "Ambulance",
                bmName = "Ambulans",
                enSound = "Nee-naw-nee-naw!",
                bmSound = "Nii-noo-nii-noo!",
                emoji = "🚑",
                imageRes = R.drawable.vehicle_ambulance,
                soundAction = { SoundEngine.playAmbulanceSiren() }
            ),
            VehicleBilingualInfo(
                id = "bicycle",
                enName = "Bicycle",
                bmName = "Basikal",
                enSound = "Ring-ring!",
                bmSound = "Kring-kring!",
                emoji = "🚲",
                imageRes = R.drawable.vehicle_bicycle,
                rawSoundId = R.raw.sound_vehicle_bicycle
            ),
            VehicleBilingualInfo(
                id = "boat",
                enName = "Boat",
                bmName = "Kapal",
                enSound = "Toot-toot!",
                bmSound = "Pooop-pooop!",
                emoji = "🚢",
                imageRes = R.drawable.vehicle_boat,
                soundAction = { SoundEngine.playBoatSound() }
            ),
            VehicleBilingualInfo(
                id = "helicopter",
                enName = "Helicopter",
                bmName = "Helikopter",
                enSound = "Chop-chop-chop!",
                bmSound = "Kup-kup-kup!",
                emoji = "🚁",
                imageRes = R.drawable.vehicle_helicopter,
                soundAction = { SoundEngine.playHelicopterSound() }
            ),
            VehicleBilingualInfo(
                id = "bus",
                enName = "Bus",
                bmName = "Bas",
                enSound = "Beep-beep!",
                bmSound = "Pon-pon!",
                emoji = "🚌",
                imageRes = R.drawable.vehicle_bus,
                soundAction = { SoundEngine.playBusSound() }
            ),
            VehicleBilingualInfo(
                id = "rocket",
                enName = "Rocket",
                bmName = "Roket",
                enSound = "Blastoff! Whoosh!",
                bmSound = "3, 2, 1 Berlepas!",
                emoji = "🚀",
                imageRes = R.drawable.vehicle_rocket,
                soundAction = { SoundEngine.playRocketSound() }
            ),
            VehicleBilingualInfo(
                id = "tractor",
                enName = "Tractor",
                bmName = "Traktor",
                enSound = "Chug-chug-chug!",
                bmSound = "Tug-tug-tug!",
                emoji = "🚜",
                imageRes = R.drawable.vehicle_tractor,
                soundAction = { SoundEngine.playTractorSound() }
            )
        )
    }

    var isQuizMode by remember { mutableStateOf(false) }
    var targetVehicleIndex by remember { mutableStateOf(0) }
    var promptMessage by remember { mutableStateOf("") }
    var showHintWiggle by remember { mutableStateOf(false) }

    fun playVehicleAudio(vehicle: VehicleBilingualInfo) {
        if (vehicle.rawSoundId != null) {
            SoundEngine.playRawSound(vehicle.rawSoundId)
        } else {
            vehicle.soundAction?.invoke()
        }
    }

    // Quiz mode repetition loop with hint wiggle
    LaunchedEffect(isQuizMode, targetVehicleIndex, LanguageManager.currentLanguage) {
        showHintWiggle = false
        if (isQuizMode) {
            val target = vehicles[targetVehicleIndex]
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
                "Sentuh kenderaan untuk dengar bunyinya & goyangkan!"
            else
                "Tap any vehicle to hear its real sound & watch it wiggle!"
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
            // Universal Header with integrated Language Toggle & Audio Settings
            ToddlerAppHeader(
                title = LanguageManager.transportationTitle(),
                subtitle = LanguageManager.transportationSubtitle(),
                onBack = onBack,
                actionSlot = {
                    ModePillButton(
                        isActive = isQuizMode,
                        activeText = LanguageManager.finding(),
                        inactiveText = LanguageManager.findMe(),
                        onClick = {
                            isQuizMode = !isQuizMode
                            if (isQuizMode) {
                                targetVehicleIndex = Random.nextInt(vehicles.size)
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

            // Vehicles Grid (Responsive 2 to 4 Columns)
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
                itemsIndexed(vehicles) { index, vehicle ->
                    val isTarget = isQuizMode && index == targetVehicleIndex
                    val name = if (LanguageManager.isMalay()) vehicle.bmName else vehicle.enName
                    val sound = if (LanguageManager.isMalay()) vehicle.bmSound else vehicle.enSound

                    VehicleCard(
                        vehicle = vehicle,
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
                                    targetVehicleIndex = (targetVehicleIndex + 1 + Random.nextInt(vehicles.size - 1)) % vehicles.size
                                } else {
                                    playVehicleAudio(vehicle)
                                    val target = vehicles[targetVehicleIndex]
                                    val targetName = if (LanguageManager.isMalay()) target.bmName else target.enName
                                    SpeechEngine.speak(
                                        if (LanguageManager.isMalay()) "Ini $name! Cari $targetName!" else "That's $name! Find $targetName!"
                                    )
                                }
                            } else {
                                playVehicleAudio(vehicle)
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
private fun VehicleCard(
    vehicle: VehicleBilingualInfo,
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

    // Visual hint wiggle in quiz mode
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
            .semantics { contentDescription = "$displayName, sound $soundText" },
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
                // Real Vehicle Photo with Rounded Corners
                RealImageOrEmoji(
                    imageRes = vehicle.imageRes,
                    emoji = vehicle.emoji,
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
