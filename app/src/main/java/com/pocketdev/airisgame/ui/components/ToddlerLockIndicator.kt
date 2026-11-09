package com.pocketdev.airisgame.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.core.LanguageManager
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.TextDark
import kotlinx.coroutines.delay

/**
 * Header Toddler Pinning Lock indicator button.
 * - Explicit 44.dp size matching design system
 * - When unlocked: Single tap requests screen pinning
 * - When locked: Requires continuous 3-second hold to initiate unlock
 * - Filling circular progress ring over 3000ms with instant cancel on release
 * - Haptic pulse + Math Challenge Parent Gate on complete hold
 */
@Composable
fun ToddlerLockIndicator(
    isPinned: Boolean,
    onLockRequested: () -> Unit,
    onUnlockConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var isHolding by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableStateOf(0f) }
    var showMathGate by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = holdProgress
    )

    // Continuous 3000ms hold timer
    LaunchedEffect(isHolding) {
        if (isHolding) {
            val startTime = System.currentTimeMillis()
            val totalTime = 3000L
            while (isHolding) {
                val elapsed = System.currentTimeMillis() - startTime
                holdProgress = (elapsed.toFloat() / totalTime).coerceIn(0f, 1f)
                if (holdProgress >= 1f) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    SoundEngine.playPop()
                    isHolding = false
                    holdProgress = 0f
                    showMathGate = true
                    break
                }
                delay(25L)
            }
        } else {
            holdProgress = 0f
        }
    }

    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                if (isPinned) Color(0xFFFEE2E2)
                else Color.White.copy(alpha = 0.94f)
            )
            .border(
                1.5.dp,
                if (isPinned) Color(0xFFFCA5A5) else Color(0xFFE2E8F0),
                CircleShape
            )
            .pointerInput(isPinned) {
                if (isPinned) {
                    detectTapGestures(
                        onPress = {
                            isHolding = true
                            tryAwaitRelease()
                            isHolding = false
                        }
                    )
                } else {
                    detectTapGestures(
                        onTap = {
                            SoundEngine.playPop()
                            onLockRequested()
                        }
                    )
                }
            }
            .semantics {
                contentDescription = if (isPinned) {
                    "Screen Pinned. Hold for 3 seconds to unlock"
                } else {
                    "Pin screen to prevent toddler exiting"
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (isPinned) {
            if (isHolding || animatedProgress > 0.01f) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.size(42.dp),
                    color = CandyCoral,
                    strokeWidth = 3.5.dp
                )
            }
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = null,
                tint = if (isHolding) CandyCoral else Color(0xFFDC2626),
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = null,
                tint = TextDark.copy(alpha = 0.55f),
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (showMathGate) {
        ParentMathGateDialog(
            title = if (LanguageManager.isMalay()) "Buka Kunci Skrin Pin" else "Unlock Screen Pinning",
            onDismiss = {
                showMathGate = false
                isHolding = false
                holdProgress = 0f
            },
            onSuccess = {
                showMathGate = false
                isHolding = false
                holdProgress = 0f
                onUnlockConfirmed()
            }
        )
    }
}
