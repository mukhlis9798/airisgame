package com.pocketdev.airisgame.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.ui.theme.TextLight

@Composable
fun ToddlerButton(
    text: String,
    buttonColor: Color,
    shadowColor: Color,
    modifier: Modifier = Modifier,
    icon: String = "",
    iconVector: ImageVector? = null,
    cornerRadius: Dp = 26.dp,
    height: Dp = 56.dp,
    contentDesc: String = text,
    testTag: String = text,
    playSoundOnTap: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val view = LocalView.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f)
    )

    val shadowOffset = if (isPressed) 2.dp else 5.dp

    Box(
        modifier = modifier
            .height(height)
            .scale(scale)
            .semantics { contentDescription = contentDesc }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                try {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                } catch (_: Exception) {}
                if (playSoundOnTap) {
                    SoundEngine.playBoing()
                }
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        // Shadow base layer matching parent size
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = shadowOffset)
                .background(shadowColor, RoundedCornerShape(cornerRadius))
        )

        // Top button surface matching parent size
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(buttonColor, RoundedCornerShape(cornerRadius)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (iconVector != null) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = TextLight,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                val label = if (icon.isNotEmpty()) "$icon  $text" else text
                Text(
                    text = label,
                    color = TextLight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
