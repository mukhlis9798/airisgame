package com.pocketdev.airisgame.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.R
import com.pocketdev.airisgame.audio.AudioSettings
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.core.LanguageManager
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.TextDark

/**
 * Universal toddler header available at the top of ALL screens.
 * Integrates:
 * - Back button (or toddler avatar on Home)
 * - Screen Title & Subtitle with responsive typography
 * - Custom Action Slot (e.g. Find Me quiz toggle, song selector)
 * - Modern Language Toggle (EN | BM, zero flags/emojis)
 * - Universal Sound Button opening the Dual-Slider AudioControlDialog
 */
@Composable
fun ToddlerAppHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actionSlot: (@Composable RowScope.() -> Unit)? = null
) {
    var showAudioDialog by remember { mutableStateOf(false) }
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val isTablet = screenWidth >= 600

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // LEFT: Back Button or Avatar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            if (onBack != null) {
                AppBackButton(
                    onBack = {
                        SoundEngine.playBoing()
                        onBack()
                    },
                    size = if (isTablet) 52.dp else 46.dp,
                    iconSize = if (isTablet) 26.dp else 22.dp
                )
            } else {
                // Home Screen Avatar
                Box(
                    modifier = Modifier
                        .size(if (isTablet) 54.dp else 48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, Color(0xFFE2E8F0), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.airis_avatar),
                        contentDescription = "Airis Avatar",
                        modifier = Modifier.size(if (isTablet) 46.dp else 40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Title & Subtitle
            Column {
                Text(
                    text = title,
                    fontSize = if (isTablet) 24.sp else if (screenWidth < 380) 18.sp else 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = if (isTablet) 13.sp else 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // RIGHT: Action Slot + Modern Language Toggle + Sound Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (screenWidth < 380) 5.dp else 8.dp)
        ) {
            actionSlot?.invoke(this)

            // Modern Language Toggle (EN | BM)
            ModernLanguageToggle()

            // Header Sound Control Button
            HeaderSoundButton(
                isAllMuted = AudioSettings.isAllMuted,
                onClick = {
                    SoundEngine.playPop()
                    showAudioDialog = true
                }
            )
        }
    }

    if (showAudioDialog) {
        AudioControlDialog(
            onDismiss = { showAudioDialog = false }
        )
    }
}

@Composable
private fun HeaderSoundButton(
    isAllMuted: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring()
    )

    Box(
        modifier = Modifier
            .size(38.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.94f))
            .border(1.5.dp, Color(0xFFE2E8F0), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .semantics { contentDescription = "Sound and Volume Settings" },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isAllMuted) "🔇" else "🔊",
            fontSize = 17.sp
        )
    }
}
