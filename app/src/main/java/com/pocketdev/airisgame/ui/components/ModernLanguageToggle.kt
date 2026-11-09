package com.pocketdev.airisgame.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.pocketdev.airisgame.core.AppLanguage
import com.pocketdev.airisgame.core.LanguageManager
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.TextDark

/**
 * Modern, clean, fresh segmented language toggle (EN | BM).
 * Completely free of flag emojis. Friendly rounded pill design that looks
 * playful yet professional for modern toddler apps.
 */
@Composable
fun ModernLanguageToggle(
    modifier: Modifier = Modifier
) {
    val isMalay = LanguageManager.isMalay()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f)
    )

    // Animated slide position for indicator pill: 0.0 for EN, 1.0 for BM
    val slidePosition by animateFloatAsState(
        targetValue = if (isMalay) 1.0f else 0.0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 380f)
    )

    val activeColor by animateColorAsState(
        targetValue = SkyVibrant,
        animationSpec = spring()
    )

    Box(
        modifier = modifier
            .height(44.dp)
            .width(80.dp)
            .scale(scale)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.94f))
            .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                SoundEngine.playPop()
                LanguageManager.toggleLanguage()
            }
            .semantics {
                contentDescription = if (isMalay) "Language: Bahasa Melayu. Tap to switch to English" else "Language: English. Tap to switch to Bahasa Melayu"
            },
        contentAlignment = Alignment.CenterStart
    ) {
        // Sliding active pill
        Box(
            modifier = Modifier
                .padding(3.dp)
                .fillMaxHeight()
                .width(36.dp)
                .offset(x = (slidePosition * 38).dp)
                .clip(RoundedCornerShape(18.dp))
                .background(activeColor)
        )

        // Labels
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // EN Option
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "EN",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (!isMalay) Color.White else TextDark.copy(alpha = 0.55f),
                    textAlign = TextAlign.Center
                )
            }

            // BM Option
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BM",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isMalay) Color.White else TextDark.copy(alpha = 0.55f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
