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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.pocketdev.airisgame.core.AppLanguage
import com.pocketdev.airisgame.core.LanguageManager
import androidx.compose.ui.platform.LocalConfiguration
import com.pocketdev.airisgame.ui.components.AppIconButton
import com.pocketdev.airisgame.ui.components.ConfettiOverlay
import com.pocketdev.airisgame.ui.components.ParentGateDialog
import com.pocketdev.airisgame.ui.components.SoundIconButton
import com.pocketdev.airisgame.ui.components.ToddlerAppHeader
import com.pocketdev.airisgame.ui.components.rememberConfettiState
import com.pocketdev.airisgame.ui.theme.BubblePink
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.CoralShadow
import com.pocketdev.airisgame.ui.theme.FreshTeal
import com.pocketdev.airisgame.ui.theme.LavenderShadow
import com.pocketdev.airisgame.ui.theme.MagicLavender
import com.pocketdev.airisgame.ui.theme.MintFresh
import com.pocketdev.airisgame.ui.theme.MintShadow
import com.pocketdev.airisgame.ui.theme.PinkShadow
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.SkyShadow
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark
import com.pocketdev.airisgame.ui.theme.TextLight
import com.pocketdev.airisgame.ui.theme.WarmOrange
import com.pocketdev.airisgame.ui.theme.YellowShadow

enum class GameDestination {
    HOME,
    ABC,
    NUMBERS,
    HIJAIYAH,
    COLOR_MATCH,
    ANIMALS,
    TRANSPORTATION,
    ABC_DOTS,
    THINGS_DOTS,
    BALLOONS,
    SHAPES,
    XYLOPHONE,
    PEEKABOO,
    DRAWING,
    PARENT_SETTINGS
}

data class ActivityCardItem(
    val titleSupplier: () -> String,
    val subtitleSupplier: () -> String,
    val icon: String,
    val bgColor: Color,
    val shadowColor: Color,
    val destination: GameDestination
)

@Composable
fun HomeScreen(
    onNavigate: (GameDestination) -> Unit
) {
    val confettiState = rememberConfettiState()
    var showParentGate by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(SoundEngine.isMuted()) }

    val infiniteTransition = rememberInfiniteTransition()
    val sunScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val activities = remember {
        listOf(
            ActivityCardItem(
                titleSupplier = { LanguageManager.abcTitle() },
                subtitleSupplier = { LanguageManager.abcSubtitle() },
                icon = "🔤",
                bgColor = SkyVibrant,
                shadowColor = SkyShadow,
                destination = GameDestination.ABC
            ),
            ActivityCardItem(
                titleSupplier = { LanguageManager.numbersTitle() },
                subtitleSupplier = { LanguageManager.numbersSubtitle() },
                icon = "🔢",
                bgColor = FreshTeal,
                shadowColor = MintShadow,
                destination = GameDestination.NUMBERS
            ),
            ActivityCardItem(
                titleSupplier = { LanguageManager.hijaiyahTitle() },
                subtitleSupplier = { LanguageManager.hijaiyahSubtitle() },
                icon = "🌙",
                bgColor = CandyCoral,
                shadowColor = CoralShadow,
                destination = GameDestination.HIJAIYAH
            ),
            ActivityCardItem(
                titleSupplier = { LanguageManager.colorMatchTitle() },
                subtitleSupplier = { LanguageManager.colorMatchSubtitle() },
                icon = "🎨",
                bgColor = SunnyYellow,
                shadowColor = YellowShadow,
                destination = GameDestination.COLOR_MATCH
            ),
            ActivityCardItem(
                titleSupplier = { LanguageManager.animalsTitle() },
                subtitleSupplier = { LanguageManager.animalsSubtitle() },
                icon = "🐱",
                bgColor = WarmOrange,
                shadowColor = CoralShadow,
                destination = GameDestination.ANIMALS
            ),
            ActivityCardItem(
                titleSupplier = { LanguageManager.transportationTitle() },
                subtitleSupplier = { LanguageManager.transportationSubtitle() },
                icon = "🚗",
                bgColor = SkyVibrant,
                shadowColor = SkyShadow,
                destination = GameDestination.TRANSPORTATION
            ),
            ActivityCardItem(
                titleSupplier = { LanguageManager.abcDotsTitle() },
                subtitleSupplier = { LanguageManager.abcDotsSubtitle() },
                icon = "✏️",
                bgColor = FreshTeal,
                shadowColor = SkyShadow,
                destination = GameDestination.ABC_DOTS
            ),
            ActivityCardItem(
                titleSupplier = { LanguageManager.thingsDotsTitle() },
                subtitleSupplier = { LanguageManager.thingsDotsSubtitle() },
                icon = "🎂",
                bgColor = CandyCoral,
                shadowColor = CoralShadow,
                destination = GameDestination.THINGS_DOTS
            ),
            ActivityCardItem(
                titleSupplier = { LanguageManager.balloonsTitle() },
                subtitleSupplier = { LanguageManager.balloonsSubtitle() },
                icon = "🎈",
                bgColor = FreshTeal,
                shadowColor = SkyShadow,
                destination = GameDestination.BALLOONS
            ),
            ActivityCardItem(
                titleSupplier = { LanguageManager.shapesTitle() },
                subtitleSupplier = { LanguageManager.shapesSubtitle() },
                icon = "⭐",
                bgColor = MintFresh,
                shadowColor = MintShadow,
                destination = GameDestination.SHAPES
            ),
            ActivityCardItem(
                titleSupplier = { LanguageManager.xylophoneTitle() },
                subtitleSupplier = { LanguageManager.xylophoneSubtitle() },
                icon = "🎹",
                bgColor = MagicLavender,
                shadowColor = LavenderShadow,
                destination = GameDestination.XYLOPHONE
            ),
            ActivityCardItem(
                titleSupplier = { LanguageManager.peekabooTitle() },
                subtitleSupplier = { LanguageManager.peekabooSubtitle() },
                icon = "🐣",
                bgColor = SunnyYellow,
                shadowColor = YellowShadow,
                destination = GameDestination.PEEKABOO
            ),
            ActivityCardItem(
                titleSupplier = { LanguageManager.drawingTitle() },
                subtitleSupplier = { LanguageManager.drawingSubtitle() },
                icon = "✨",
                bgColor = BubblePink,
                shadowColor = PinkShadow,
                destination = GameDestination.DRAWING
            )
        )
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
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp
            val gridColumns = when {
                screenWidth >= 840 -> 4
                screenWidth >= 600 -> 3
                else -> 2
            }

            // Universal Top Bar with Avatar, Title, Settings, Language Toggle & Sound Button
            ToddlerAppHeader(
                title = LanguageManager.appTitle(),
                subtitle = LanguageManager.appSubtitle(),
                onBack = null,
                actionSlot = {
                    AppIconButton(
                        icon = Icons.Rounded.Settings,
                        contentDescription = "Parent Settings",
                        size = 38.dp,
                        iconSize = 22.dp,
                        onClick = {
                            SoundEngine.playSparkle()
                            showParentGate = true
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Activities Grid (Responsive Columns: 2 for phone, 3-4 for tablet/landscape)
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(activities) { item ->
                    val title = item.titleSupplier()
                    val subtitle = item.subtitleSupplier()

                    ActivityTile(
                        title = title,
                        subtitle = subtitle,
                        icon = item.icon,
                        bgColor = item.bgColor,
                        shadowColor = item.shadowColor,
                        onClick = {
                            SoundEngine.playBoing()
                            SpeechEngine.speak(title)
                            onNavigate(item.destination)
                        }
                    )
                }
            }
        }

        // Confetti effect on top
        ConfettiOverlay(state = confettiState)

        // Parent Gate Modal
        if (showParentGate) {
            ParentGateDialog(
                onDismiss = { showParentGate = false },
                onSuccess = {
                    showParentGate = false
                    onNavigate(GameDestination.PARENT_SETTINGS)
                }
            )
        }
    }
}

@Composable
private fun ActivityTile(
    title: String,
    subtitle: String,
    icon: String,
    bgColor: Color,
    shadowColor: Color,
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
            .height(145.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .semantics { contentDescription = "$title game" },
        contentAlignment = Alignment.Center
    ) {
        // Shadow layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 6.dp)
                .background(shadowColor, RoundedCornerShape(26.dp))
        )

        // Main Surface
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 44.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (bgColor == SunnyYellow || bgColor == MintFresh) TextDark else TextLight,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (bgColor == SunnyYellow || bgColor == MintFresh) TextDark.copy(alpha = 0.75f) else TextLight.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
