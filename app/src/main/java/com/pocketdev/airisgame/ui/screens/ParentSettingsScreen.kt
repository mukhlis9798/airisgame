package com.pocketdev.airisgame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.ui.components.ToddlerButton
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.CoralShadow
import com.pocketdev.airisgame.ui.theme.MagicLavender
import androidx.compose.foundation.layout.statusBarsPadding
import com.pocketdev.airisgame.ui.components.AppBackButton
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.SkyShadow
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark

@Composable
fun ParentSettingsScreen(onBack: () -> Unit) {
    var speechRate by remember { mutableStateOf(SpeechEngine.speechRate) }
    var pitch by remember { mutableStateOf(SpeechEngine.pitch) }

    val scrollState = rememberScrollState()

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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppBackButton(
                    onBack = {
                        SoundEngine.playBoing()
                        onBack()
                    }
                )

                Text(
                    text = if (com.pocketdev.airisgame.core.LanguageManager.isMalay()) "Pusat Ibu Bapa" else "Parent Dashboard",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.size(52.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Audio & Voice Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔊 " + if (com.pocketdev.airisgame.core.LanguageManager.isMalay()) "Kawalan Audio & Suara" else "Sound & Voice Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Voice & Learning Volume
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🗣️ " + com.pocketdev.airisgame.core.LanguageManager.voiceVolumeLabel(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                            Text(
                                text = com.pocketdev.airisgame.core.LanguageManager.voiceVolumeSub(),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = !com.pocketdev.airisgame.audio.AudioSettings.isVoiceMuted,
                            onCheckedChange = { checked ->
                                com.pocketdev.airisgame.audio.AudioSettings.isVoiceMuted = !checked
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SkyVibrant
                            )
                        )
                    }

                    Slider(
                        value = if (com.pocketdev.airisgame.audio.AudioSettings.isVoiceMuted) 0f else com.pocketdev.airisgame.audio.AudioSettings.voiceVolume,
                        onValueChange = {
                            com.pocketdev.airisgame.audio.AudioSettings.voiceVolume = it
                            if (com.pocketdev.airisgame.audio.AudioSettings.isVoiceMuted && it > 0.05f) {
                                com.pocketdev.airisgame.audio.AudioSettings.isVoiceMuted = false
                            }
                        },
                        valueRange = 0.0f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = SkyVibrant,
                            activeTrackColor = SkyVibrant
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sound Effects & Fun Volume
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🎵 " + com.pocketdev.airisgame.core.LanguageManager.sfxVolumeLabel(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                            Text(
                                text = com.pocketdev.airisgame.core.LanguageManager.sfxVolumeSub(),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = !com.pocketdev.airisgame.audio.AudioSettings.isSfxMuted,
                            onCheckedChange = { checked ->
                                com.pocketdev.airisgame.audio.AudioSettings.isSfxMuted = !checked
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CandyCoral
                            )
                        )
                    }

                    Slider(
                        value = if (com.pocketdev.airisgame.audio.AudioSettings.isSfxMuted) 0f else com.pocketdev.airisgame.audio.AudioSettings.sfxVolume,
                        onValueChange = {
                            com.pocketdev.airisgame.audio.AudioSettings.sfxVolume = it
                            if (com.pocketdev.airisgame.audio.AudioSettings.isSfxMuted && it > 0.05f) {
                                com.pocketdev.airisgame.audio.AudioSettings.isSfxMuted = false
                            }
                        },
                        valueRange = 0.0f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = CandyCoral,
                            activeTrackColor = CandyCoral
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Speech Rate Slider
                    Text(
                        text = (if (com.pocketdev.airisgame.core.LanguageManager.isMalay()) "Kelajuan Sebutan: " else "Speech Pace: ") +
                                "${(speechRate * 100).toInt()}% " +
                                (if (com.pocketdev.airisgame.core.LanguageManager.isMalay()) "(Perlahan lebih jelas untuk usia 2 tahun)" else "(Slower is clearer for toddlers)"),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Slider(
                        value = speechRate,
                        onValueChange = {
                            speechRate = it
                            SpeechEngine.speechRate = it
                        },
                        valueRange = 0.6f..1.1f,
                        colors = SliderDefaults.colors(
                            thumbColor = SkyVibrant,
                            activeTrackColor = SkyVibrant
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Pitch Slider
                    Text(
                        text = (if (com.pocketdev.airisgame.core.LanguageManager.isMalay()) "Ketinggian Nada: " else "Voice Pitch: ") +
                                "${(pitch * 100).toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Slider(
                        value = pitch,
                        onValueChange = {
                            pitch = it
                            SpeechEngine.pitch = it
                        },
                        valueRange = 0.9f..1.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = MagicLavender,
                            activeTrackColor = MagicLavender
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ToddlerButton(
                            text = com.pocketdev.airisgame.core.LanguageManager.testVoiceBtn(),
                            buttonColor = SkyVibrant,
                            shadowColor = SkyShadow,
                            modifier = Modifier.weight(1f),
                            height = 46.dp,
                            onClick = {
                                SpeechEngine.speak(
                                    if (com.pocketdev.airisgame.core.LanguageManager.isMalay())
                                        "Hai Airis! Hebatnya belajar hari ini!"
                                    else
                                        "Hello Airis! Great job learning today!"
                                )
                            }
                        )

                        ToddlerButton(
                            text = com.pocketdev.airisgame.core.LanguageManager.testSfxBtn(),
                            buttonColor = CandyCoral,
                            shadowColor = CoralShadow,
                            modifier = Modifier.weight(1f),
                            height = 46.dp,
                            onClick = {
                                SoundEngine.playCelebrationFanfare()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Language Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🌐 " + if (com.pocketdev.airisgame.core.LanguageManager.isMalay()) "Bahasa Pembelajaran" else "Learning Language",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (com.pocketdev.airisgame.core.LanguageManager.isMalay())
                            "Pilih bahasa untuk perkataan, sebutan fonik dan audio."
                        else
                            "Choose language for words, phonics pronunciation, and audio.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ToddlerButton(
                            text = "English",
                            buttonColor = if (!com.pocketdev.airisgame.core.LanguageManager.isMalay()) SkyVibrant else Color(0xFFE2E8F0),
                            shadowColor = if (!com.pocketdev.airisgame.core.LanguageManager.isMalay()) SkyShadow else Color(0xFFCBD5E1),
                            modifier = Modifier.weight(1f),
                            height = 50.dp,
                            onClick = {
                                com.pocketdev.airisgame.core.LanguageManager.currentLanguage = com.pocketdev.airisgame.core.AppLanguage.EN
                                SpeechEngine.speak("English mode selected!")
                            }
                        )
                        ToddlerButton(
                            text = "Bahasa Melayu",
                            buttonColor = if (com.pocketdev.airisgame.core.LanguageManager.isMalay()) CandyCoral else Color(0xFFE2E8F0),
                            shadowColor = if (com.pocketdev.airisgame.core.LanguageManager.isMalay()) CoralShadow else Color(0xFFCBD5E1),
                            modifier = Modifier.weight(1f),
                            height = 50.dp,
                            onClick = {
                                com.pocketdev.airisgame.core.LanguageManager.currentLanguage = com.pocketdev.airisgame.core.AppLanguage.BM
                                SpeechEngine.speak("Mod Bahasa Melayu dipilih!")
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toddler Developmental Guide
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🌱 " + if (com.pocketdev.airisgame.core.LanguageManager.isMalay()) "Matlamat Pembelajaran (Umur 2 Tahun)" else "Toddler Learning Goals (Age 2)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (com.pocketdev.airisgame.core.LanguageManager.isMalay())
                            "• Fonik ABC: Mengenal bentuk huruf dan membina asas sebutan fonik.\n" +
                            "• Hijaiyah: Mengenal huruf Arab dan baris Fathah, Kasrah, Dammah.\n" +
                            "• Padanan Warna: Mengasah diskriminasi visual melalui tarikan magnetik.\n" +
                            "• Bunyi Haiwan: Menggalakkan peniruan suara dan kepekaan fonologi.\n" +
                            "• Belon & Telur: Melatih koordinasi tangan-mata dan rasa ingin tahu."
                        else
                            "• ABC Phonics: Encourages speech imitation and phonological awareness.\n" +
                            "• Hijaiyah: Introduces Arabic alphabet and Fathah, Kasrah, Dammah vowels.\n" +
                            "• Color Snap: Sharpens visual discrimination with magnetic snapping.\n" +
                            "• Pop Balloons: Fosters hand-eye coordination and playful discovery.\n" +
                            "• Xylophone & Eggs: Develops patience, rhythm, and cause-and-effect.",
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = Color(0xFF4A5568)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ToddlerButton(
                text = if (com.pocketdev.airisgame.core.LanguageManager.isMalay()) "Kembali Bermain" else "Resume Playing",
                buttonColor = CandyCoral,
                shadowColor = CoralShadow,
                modifier = Modifier.fillMaxWidth(),
                height = 56.dp,
                onClick = onBack
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
