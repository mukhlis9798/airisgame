package com.pocketdev.airisgame.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.core.BedtimeTimerManager
import com.pocketdev.airisgame.core.LanguageManager
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.CoralShadow
import com.pocketdev.airisgame.ui.theme.MagicLavender
import com.pocketdev.airisgame.ui.theme.SunnyYellow

@Composable
fun BedtimeOverlay() {
    var showParentUnlockGate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        SoundEngine.playLullabyMelody()
        val prompt = if (LanguageManager.isMalay())
            "Masa bermain sudah tamat. Jom rehat dan tidur, Airis!"
        else
            "Playtime is finished. Time to rest and sleep, Airis!"
        SpeechEngine.speak(prompt)
    }

    val infiniteTransition = rememberInfiniteTransition()
    val moonScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400),
            repeatMode = RepeatMode.Reverse
        )
    )

    val nightGradient = Brush.verticalGradient(
        listOf(
            Color(0xFF0D1B2A),
            Color(0xFF1B263B),
            Color(0xFF415A77)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(nightGradient)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🌙",
                fontSize = 80.sp,
                modifier = Modifier.scale(moonScale)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (LanguageManager.isMalay()) "Masa Untuk Tidur & Rehat" else "Bedtime & Sweet Dreams",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SunnyYellow,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (LanguageManager.isMalay())
                    "Tahniah Airis! Hari ini hebat sangat bermain & belajar. Sekarang masa untuk rehat. Selamat malam!"
                else
                    "Great job today, Airis! You learned and played so well. Now it's time to rest your eyes. Good night!",
                fontSize = 15.sp,
                color = Color(0xFFE0E1DD),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "🧸 ✨ 💤",
                fontSize = 44.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Subtle Parent Control Unlock Button
            ToddlerButton(
                text = if (LanguageManager.isMalay()) "Ibu Bapa: Buka Kunci" else "Parent: Unlock Timer",
                buttonColor = MagicLavender.copy(alpha = 0.85f),
                shadowColor = Color(0xFF5A189A),
                height = 46.dp,
                onClick = {
                    showParentUnlockGate = true
                }
            )
        }
    }

    if (showParentUnlockGate) {
        ParentMathGateDialog(
            title = if (LanguageManager.isMalay()) "Buka Kunci Masa Tidur" else "Unlock Bedtime Timer",
            onDismiss = { showParentUnlockGate = false },
            onSuccess = {
                showParentUnlockGate = false
                BedtimeTimerManager.turnOff()
            }
        )
    }
}
