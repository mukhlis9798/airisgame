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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.pocketdev.airisgame.core.ScreenPinningManager
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.core.BedtimeTimerManager
import com.pocketdev.airisgame.ui.components.BedtimeOverlay
import com.pocketdev.airisgame.ui.screens.AbcDotsScreen
import com.pocketdev.airisgame.ui.screens.AbcScreen
import com.pocketdev.airisgame.ui.screens.AnimalsScreen
import com.pocketdev.airisgame.ui.screens.BalloonsScreen
import com.pocketdev.airisgame.ui.screens.ColorMatchScreen
import com.pocketdev.airisgame.ui.screens.DrawingScreen
import com.pocketdev.airisgame.ui.screens.GameDestination
import com.pocketdev.airisgame.ui.screens.HijaiyahScreen
import com.pocketdev.airisgame.ui.screens.HomeScreen
import com.pocketdev.airisgame.ui.screens.NumbersScreen
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

        enableImmersiveMode()

        ScreenPinningManager.onLockRequested = { enableScreenPinning() }
        ScreenPinningManager.onUnlockConfirmed = { disableScreenPinning() }

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

    private fun enableImmersiveMode() {
        try {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } catch (_: Exception) {}
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            enableImmersiveMode()
        }
    }

    fun enableScreenPinning() {
        try {
            startLockTask()
            ScreenPinningManager.isPinned = true
        } catch (_: Exception) {
            // Handle devices where lock task is restricted
        }
    }

    fun disableScreenPinning() {
        try {
            stopLockTask()
            ScreenPinningManager.isPinned = false
        } catch (_: Exception) {
            // Handle already stopped state
        }
    }

    override fun onPause() {
        super.onPause()
        SoundEngine.pauseAll()
    }

    override fun onResume() {
        super.onResume()
        SoundEngine.resumeAll()
        enableImmersiveMode()
    }

    override fun onDestroy() {
        super.onDestroy()
        SpeechEngine.shutdown()
        SoundEngine.release()
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

    Box(modifier = Modifier.fillMaxSize()) {
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
                GameDestination.NUMBERS -> {
                    NumbersScreen(
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

        // Bedtime / Screen Time Limit Overlay
        if (BedtimeTimerManager.isBedtimeActive) {
            BedtimeOverlay()
        }
    }
}
