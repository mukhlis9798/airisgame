package com.pocketdev.airisgame.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.CoralShadow
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark

/**
 * Toddler-friendly round action button with tactile spring animation,
 * shadow base, and Material 3 vector icon.
 */
@Composable
fun AppIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String,
    containerColor: Color = Color.White,
    iconColor: Color = TextDark,
    size: Dp = 52.dp,
    iconSize: Dp = 26.dp,
    playSound: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val view = LocalView.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 450f)
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .semantics { this.contentDescription = contentDescription }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                try {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                } catch (_: Exception) {}
                if (playSound) {
                    SoundEngine.playBoing()
                }
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        // Shadow base layer
        Box(
            modifier = Modifier
                .size(size)
                .offset(y = 4.dp)
                .clip(CircleShape)
                .background(Color(0x33000000))
        )

        // Main surface
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

/**
 * Standard back navigation button across all screens using Icons.Rounded.ArrowBack.
 */
@Composable
fun AppBackButton(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Go back",
    size: androidx.compose.ui.unit.Dp = 52.dp,
    iconSize: androidx.compose.ui.unit.Dp = 26.dp
) {
    AppIconButton(
        icon = Icons.Rounded.ArrowBack,
        onClick = onBack,
        modifier = modifier,
        contentDescription = contentDescription,
        size = size,
        iconSize = iconSize
    )
}

/**
 * Sound Mute / Unmute Button with clean custom vector drawing.
 */
@Composable
fun SoundIconButton(
    isMuted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val view = LocalView.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 450f)
    )

    Box(
        modifier = modifier
            .size(52.dp)
            .scale(scale)
            .semantics { contentDescription = if (isMuted) "Unmute sound" else "Mute sound" }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                try {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                } catch (_: Exception) {}
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        // Shadow base
        Box(
            modifier = Modifier
                .size(52.dp)
                .offset(y = 4.dp)
                .clip(CircleShape)
                .background(Color(0x33000000))
        )

        // Surface
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (isMuted) Color(0xFFFFF0F0) else Color.White),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(26.dp)) {
                val w = size.width
                val h = size.height

                // Speaker body
                val speakerPath = Path().apply {
                    moveTo(w * 0.15f, h * 0.35f)
                    lineTo(w * 0.35f, h * 0.35f)
                    lineTo(w * 0.58f, h * 0.15f)
                    lineTo(w * 0.58f, h * 0.85f)
                    lineTo(w * 0.35f, h * 0.65f)
                    lineTo(w * 0.15f, h * 0.65f)
                    close()
                }

                val iconColor = if (isMuted) CandyCoral else TextDark
                drawPath(speakerPath, color = iconColor)

                if (isMuted) {
                    // Mute diagonal slash
                    drawLine(
                        color = CandyCoral,
                        start = Offset(w * 0.70f, h * 0.30f),
                        end = Offset(w * 0.95f, h * 0.70f),
                        strokeWidth = 3.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = CandyCoral,
                        start = Offset(w * 0.95f, h * 0.30f),
                        end = Offset(w * 0.70f, h * 0.70f),
                        strokeWidth = 3.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                } else {
                    // Sound waves
                    val waveStroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    drawArc(
                        color = iconColor,
                        startAngle = -45f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset(w * 0.40f, h * 0.28f),
                        size = androidx.compose.ui.geometry.Size(w * 0.35f, h * 0.44f),
                        style = waveStroke
                    )
                    drawArc(
                        color = iconColor,
                        startAngle = -50f,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = Offset(w * 0.45f, h * 0.12f),
                        size = androidx.compose.ui.geometry.Size(w * 0.50f, h * 0.76f),
                        style = waveStroke
                    )
                }
            }
        }
    }
}

/**
 * Quiz / Game Mode Pill Button with Vector Icon.
 */
@Composable
fun ModePillButton(
    isActive: Boolean,
    activeText: String,
    inactiveText: String,
    activeIcon: ImageVector = Icons.Rounded.Search,
    inactiveIcon: ImageVector = Icons.Rounded.Search,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f)
    )

    Box(
        modifier = modifier
            .scale(scale)
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (isActive) SunnyYellow else Color.White)
            .border(1.5.dp, if (isActive) SunnyYellow else Color(0xFFE2E8F0), RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .padding(horizontal = 12.dp)
            .semantics { contentDescription = if (isActive) activeText else inactiveText },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isActive) activeIcon else inactiveIcon,
                contentDescription = null,
                tint = TextDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isActive) activeText else inactiveText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }
    }
}
