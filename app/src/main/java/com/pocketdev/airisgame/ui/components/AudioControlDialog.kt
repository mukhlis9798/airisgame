package com.pocketdev.airisgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pocketdev.airisgame.audio.AudioSettings
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.core.LanguageManager
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.CoralShadow
import com.pocketdev.airisgame.ui.theme.SkyShadow
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark
import com.pocketdev.airisgame.ui.theme.WarmOrange

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun AudioControlDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = LanguageManager.audioDialogTitle(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextDark
                            )
                            Text(
                                text = if (LanguageManager.isMalay()) "Sesuaikan kelantangan audio" else "Adjust volume levels",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        // Close button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                                .clickable {
                                    SoundEngine.playBoing()
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close",
                                tint = TextDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // SECTION 1: Voice & Learning Volume (ABC Phonics, Letters, Xylophone)
                    VolumeControlCard(
                        title = LanguageManager.voiceVolumeLabel(),
                        subtitle = LanguageManager.voiceVolumeSub(),
                        iconLabel = "🗣️",
                        volume = AudioSettings.voiceVolume,
                        isMuted = AudioSettings.isVoiceMuted,
                        accentColor = SkyVibrant,
                        onVolumeChange = {
                            AudioSettings.voiceVolume = it
                            if (AudioSettings.isVoiceMuted && it > 0.05f) {
                                AudioSettings.isVoiceMuted = false
                            }
                        },
                        onToggleMute = {
                            AudioSettings.toggleVoiceMute()
                            if (!AudioSettings.isVoiceMuted) {
                                SoundEngine.playSparkle()
                            }
                        },
                        testButtonText = LanguageManager.testVoiceBtn(),
                        onTest = {
                            SpeechEngine.speak(
                                if (LanguageManager.isMalay()) "Hai Airis! Jom belajar sama-sama!" else "Hello Airis! Let's learn together!"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // SECTION 2: Sound Effects & Fun (Animals, Vehicles, Pops, Celebrations)
                    VolumeControlCard(
                        title = LanguageManager.sfxVolumeLabel(),
                        subtitle = LanguageManager.sfxVolumeSub(),
                        iconLabel = "🎵",
                        volume = AudioSettings.sfxVolume,
                        isMuted = AudioSettings.isSfxMuted,
                        accentColor = CandyCoral,
                        onVolumeChange = {
                            AudioSettings.sfxVolume = it
                            if (AudioSettings.isSfxMuted && it > 0.05f) {
                                AudioSettings.isSfxMuted = false
                            }
                        },
                        onToggleMute = {
                            AudioSettings.toggleSfxMute()
                            if (!AudioSettings.isSfxMuted) {
                                SoundEngine.playSparkle()
                            }
                        },
                        testButtonText = LanguageManager.testSfxBtn(),
                        onTest = {
                            SoundEngine.playCelebrationFanfare()
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Done Button
                    ToddlerButton(
                        text = LanguageManager.doneBtn(),
                        buttonColor = SkyVibrant,
                        shadowColor = SkyShadow,
                        modifier = Modifier.fillMaxWidth(),
                        height = 50.dp,
                        onClick = {
                            SoundEngine.playBoing()
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VolumeControlCard(
    title: String,
    subtitle: String,
    iconLabel: String,
    volume: Float,
    isMuted: Boolean,
    accentColor: Color,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    testButtonText: String,
    onTest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Title & Mute Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = iconLabel, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }

                // Mute / Unmute Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isMuted) Color(0xFFFFE4E6) else accentColor.copy(alpha = 0.15f))
                        .border(
                            1.dp,
                            if (isMuted) CandyCoral.copy(alpha = 0.4f) else accentColor.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onToggleMute() }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isMuted) (if (LanguageManager.isMalay()) "Bisu" else "Muted") else (if (LanguageManager.isMalay()) "Aktif" else "Active"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMuted) CandyCoral else accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Slider & Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = if (isMuted) 0f else volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0.0f..1.0f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = Color(0xFFE2E8F0)
                    )
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "${if (isMuted) 0 else (volume * 100).toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.width(40.dp)
                )
            }

            // Quick Test Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                        .clickable { onTest() }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "▶ $testButtonText",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                }
            }
        }
    }
}
