package com.pocketdev.airisgame.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.core.LanguageManager
import com.pocketdev.airisgame.ui.theme.CandyCoral
import kotlinx.coroutines.delay

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

    val animatedProgress by animateFloatAsState(targetValue = holdProgress)

    // 3000ms hold timer
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
                delay(30L)
            }
        } else {
            holdProgress = 0f
        }
    }

    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            // Low-contrast semi-transparent padlock that does not distract toddlers
            .background(
                if (isPinned) Color.Black.copy(alpha = 0.12f)
                else Color.White.copy(alpha = 0.25f)
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
                            onLockRequested()
                        }
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (isPinned) {
            if (isHolding) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.size(42.dp),
                    color = CandyCoral,
                    strokeWidth = 3.dp
                )
            }
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = "Screen Pinned. Hold 3s to unlock",
                tint = if (isHolding) CandyCoral else Color.White.copy(alpha = 0.55f),
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = "Pin Screen for Toddler",
                tint = Color.White.copy(alpha = 0.35f),
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (showMathGate) {
        ParentMathGateDialog(
            title = if (LanguageManager.isMalay()) "Buka Kunci Skrin Pin" else "Unlock Screen Pinning",
            onDismiss = { showMathGate = false },
            onSuccess = {
                showMathGate = false
                onUnlockConfirmed()
            }
        )
    }
}
