package com.pocketdev.airisgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.ui.screens.AbcDotsScreen
import com.pocketdev.airisgame.ui.screens.AbcScreen
import com.pocketdev.airisgame.ui.screens.AnimalsScreen
import com.pocketdev.airisgame.ui.screens.BalloonsScreen
import com.pocketdev.airisgame.ui.screens.ColorMatchScreen
import com.pocketdev.airisgame.ui.screens.DrawingScreen
import com.pocketdev.airisgame.ui.screens.GameDestination
import com.pocketdev.airisgame.ui.screens.HijaiyahScreen
import com.pocketdev.airisgame.ui.screens.HomeScreen
import com.pocketdev.airisgame.ui.screens.ParentSettingsScreen
import com.pocketdev.airisgame.ui.screens.PeekabooScreen
import com.pocketdev.airisgame.ui.screens.ShapesScreen
import com.pocketdev.airisgame.ui.screens.ThingsDotsScreen
import com.pocketdev.airisgame.ui.screens.TransportationScreen
import com.pocketdev.airisgame.ui.screens.XylophoneScreen
import com.pocketdev.airisgame.ui.theme.AirisGameTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Speech and Sound Engines for clear toddler audio
        SpeechEngine.init(this)
        SoundEngine.init(this)

        setContent {
            AirisGameTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AirisAppRoot()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SpeechEngine.shutdown()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AirisAppRoot() {
    var currentScreen by remember { mutableStateOf(GameDestination.HOME) }

    // Intercept back navigation when inside any sub-game
    BackHandler(enabled = currentScreen != GameDestination.HOME) {
        currentScreen = GameDestination.HOME
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn() with fadeOut()
        }
    ) { destination ->
        when (destination) {
            GameDestination.HOME -> {
                HomeScreen(
                    onNavigate = { currentScreen = it }
                )
            }
            GameDestination.ABC -> {
                AbcScreen(
                    onBack = { currentScreen = GameDestination.HOME }
                )
            }
            GameDestination.HIJAIYAH -> {
                HijaiyahScreen(
                    onBack = { currentScreen = GameDestination.HOME }
                )
            }
            GameDestination.COLOR_MATCH -> {
                ColorMatchScreen(
                    onBack = { currentScreen = GameDestination.HOME }
                )
            }
            GameDestination.ANIMALS -> {
                AnimalsScreen(
                    onBack = { currentScreen = GameDestination.HOME }
                )
            }
            GameDestination.TRANSPORTATION -> {
                TransportationScreen(
                    onBack = { currentScreen = GameDestination.HOME }
                )
            }
            GameDestination.ABC_DOTS -> {
                AbcDotsScreen(
                    onBack = { currentScreen = GameDestination.HOME }
                )
            }
            GameDestination.THINGS_DOTS -> {
                ThingsDotsScreen(
                    onBack = { currentScreen = GameDestination.HOME }
                )
            }
            GameDestination.BALLOONS -> {
                BalloonsScreen(
                    onBack = { currentScreen = GameDestination.HOME }
                )
            }
            GameDestination.SHAPES -> {
                ShapesScreen(
                    onBack = { currentScreen = GameDestination.HOME }
                )
            }
            GameDestination.XYLOPHONE -> {
                XylophoneScreen(
                    onBack = { currentScreen = GameDestination.HOME }
                )
            }
            GameDestination.PEEKABOO -> {
                PeekabooScreen(
                    onBack = { currentScreen = GameDestination.HOME }
                )
            }
            GameDestination.DRAWING -> {
                DrawingScreen(
                    onBack = { currentScreen = GameDestination.HOME }
                )
            }
            GameDestination.PARENT_SETTINGS -> {
                ParentSettingsScreen(
                    onBack = { currentScreen = GameDestination.HOME }
                )
            }
        }
    }
}
