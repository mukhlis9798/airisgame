package com.pocketdev.airisgame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.airisgame.audio.AudioSettings
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.audio.SpeechEngine
import com.pocketdev.airisgame.core.AppLanguage
import com.pocketdev.airisgame.core.BedtimeTimerManager
import com.pocketdev.airisgame.core.LanguageManager
import com.pocketdev.airisgame.ui.components.AppBackButton
import com.pocketdev.airisgame.ui.components.ToddlerButton
import com.pocketdev.airisgame.ui.theme.BubblePink
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.CoralShadow
import com.pocketdev.airisgame.ui.theme.MagicLavender
import com.pocketdev.airisgame.ui.theme.MintFresh
import com.pocketdev.airisgame.ui.theme.SkyGradient
import com.pocketdev.airisgame.ui.theme.SkyShadow
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.SunnyYellow
import com.pocketdev.airisgame.ui.theme.TextDark

@Composable
fun ParentSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
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
                    text = if (LanguageManager.isMalay()) "Pusat Ibu Bapa" else "Parent Dashboard",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.size(52.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Bedtime & Screen Time Timer Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🌙 " + if (LanguageManager.isMalay()) "Had Masa Skrin & Masa Tidur" else "Screen Time & Bedtime Limit",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (LanguageManager.isMalay())
                            "Kunci aplikasi secara lembut dengan animasi tidur dan lagu santai apabila masa bermain tamat."
                        else
                            "Gently locks the app with calming animations and lullabies when screen time expires.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Timer Options Chips
                    val timerOptions = listOf(0, 15, 30, 45, 60)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        timerOptions.forEach { minutes ->
                            val isSelected = BedtimeTimerManager.timerDurationMinutes == minutes
                            val label = if (minutes == 0) {
                                if (LanguageManager.isMalay()) "Mati" else "Off"
                            } else {
                                "$minutes m"
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) MagicLavender else Color(0xFFF1F5F9))
                                    .border(
                                        1.5.dp,
                                        if (isSelected) Color(0xFF7C4DFF) else Color(0xFFE2E8F0),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        SoundEngine.playPop()
                                        BedtimeTimerManager.setTimer(minutes)
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextDark
                                )
                            }
                        }
                    }

                    if (BedtimeTimerManager.timerDurationMinutes > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        val mins = BedtimeTimerManager.remainingSeconds / 60
                        val secs = BedtimeTimerManager.remainingSeconds % 60
                        Text(
                            text = if (LanguageManager.isMalay())
                                "⏳ Masa bermain berbaki: $mins minit $secs saat"
                            else
                                "⏳ Playtime remaining: $mins min $secs sec",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CandyCoral
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Audio & Voice Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔊 " + if (LanguageManager.isMalay()) "Kawalan Audio & Suara" else "Sound & Voice Settings",
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
                                text = "🗣️ " + LanguageManager.voiceVolumeLabel(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                            Text(
                                text = LanguageManager.voiceVolumeSub(),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = !AudioSettings.isVoiceMuted,
                            onCheckedChange = { checked ->
                                AudioSettings.isVoiceMuted = !checked
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SkyVibrant
                            )
                        )
                    }

                    Slider(
                        value = if (AudioSettings.isVoiceMuted) 0f else AudioSettings.voiceVolume,
                        onValueChange = {
                            AudioSettings.voiceVolume = it
                            if (AudioSettings.isVoiceMuted && it > 0.05f) {
                                AudioSettings.isVoiceMuted = false
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
                                text = "🎵 " + LanguageManager.sfxVolumeLabel(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                            Text(
                                text = LanguageManager.sfxVolumeSub(),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = !AudioSettings.isSfxMuted,
                            onCheckedChange = { checked ->
                                AudioSettings.isSfxMuted = !checked
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CandyCoral
                            )
                        )
                    }

                    Slider(
                        value = if (AudioSettings.isSfxMuted) 0f else AudioSettings.sfxVolume,
                        onValueChange = {
                            AudioSettings.sfxVolume = it
                            if (AudioSettings.isSfxMuted && it > 0.05f) {
                                AudioSettings.isSfxMuted = false
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
                        text = (if (LanguageManager.isMalay()) "Kelajuan Sebutan: " else "Speech Pace: ") +
                                "${(speechRate * 100).toInt()}% " +
                                (if (LanguageManager.isMalay()) "(Perlahan lebih jelas untuk usia 2 tahun)" else "(Slower is clearer for toddlers)"),
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
                        text = (if (LanguageManager.isMalay()) "Ketinggian Nada: " else "Voice Pitch: ") +
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
                            text = LanguageManager.testVoiceBtn(),
                            buttonColor = SkyVibrant,
                            shadowColor = SkyShadow,
                            modifier = Modifier.weight(1f),
                            height = 46.dp,
                            onClick = {
                                SpeechEngine.speak(
                                    if (LanguageManager.isMalay())
                                        "Hai Airis! Hebatnya belajar hari ini!"
                                    else
                                        "Hello Airis! Great job learning today!"
                                )
                            }
                        )

                        ToddlerButton(
                            text = LanguageManager.testSfxBtn(),
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

            // 3. TTS Voice Package Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎙️ " + if (LanguageManager.isMalay()) "Status Pakej Suara TTS" else "TTS Voice Package Status",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val malayStatusText = if (SpeechEngine.isMalayTtsAvailable)
                        (if (LanguageManager.isMalay()) "✅ Pakej Suara Bahasa Melayu sedia dipasang." else "✅ Malay Voice Data installed & ready.")
                    else
                        (if (LanguageManager.isMalay()) "⚠️ Pakej Suara Bahasa Melayu belum dipasang (menggunakan suara sistem lalai)." else "⚠️ Malay Voice Data not downloaded (using system default).")

                    Text(
                        text = malayStatusText,
                        fontSize = 13.5.sp,
                        color = if (SpeechEngine.isMalayTtsAvailable) Color(0xFF2E7D32) else CandyCoral,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ToddlerButton(
                        text = if (LanguageManager.isMalay()) "Muat Turun Data Suara Google TTS" else "Download Voice Data in Settings",
                        buttonColor = SkyVibrant,
                        shadowColor = SkyShadow,
                        height = 44.dp,
                        onClick = {
                            SpeechEngine.openInstallTtsDataIntent(context)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Language Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🌐 " + if (LanguageManager.isMalay()) "Bahasa Pembelajaran" else "Learning Language",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (LanguageManager.isMalay())
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
                            buttonColor = if (!LanguageManager.isMalay()) SkyVibrant else Color(0xFFE2E8F0),
                            shadowColor = if (!LanguageManager.isMalay()) SkyShadow else Color(0xFFCBD5E1),
                            modifier = Modifier.weight(1f),
                            height = 50.dp,
                            onClick = {
                                LanguageManager.currentLanguage = AppLanguage.EN
                                SpeechEngine.speak("English mode selected!")
                            }
                        )
                        ToddlerButton(
                            text = "Bahasa Melayu",
                            buttonColor = if (LanguageManager.isMalay()) CandyCoral else Color(0xFFE2E8F0),
                            shadowColor = if (LanguageManager.isMalay()) CoralShadow else Color(0xFFCBD5E1),
                            modifier = Modifier.weight(1f),
                            height = 50.dp,
                            onClick = {
                                LanguageManager.currentLanguage = AppLanguage.BM
                                SpeechEngine.speak("Mod Bahasa Melayu dipilih!")
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Toddler Developmental Guide
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🌱 " + if (LanguageManager.isMalay()) "Matlamat Pembelajaran (Umur 2-4 Tahun)" else "Toddler Learning Goals (Age 2-4)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (LanguageManager.isMalay())
                            "• Fonik ABC & 123 Nombor: Mengenal bentuk huruf, angka, dan membina asas sebutan fonik & mengira.\n" +
                            "• Hijaiyah: Mengenal huruf Arab dan baris Fathah, Kasrah, Dammah.\n" +
                            "• Padanan 7 Warna Pelangi: Mengasah diskriminasi visual & tarikan magnetik.\n" +
                            "• Bunyi Haiwan & Kenderaan: Menggalakkan peniruan suara dan kepekaan fonologi.\n" +
                            "• Sambung Titik Splin: Melatih kawalan motor halus dan koordinasi pensel.\n" +
                            "• Skrin Pin Kanak-kanak: Memastikan anak bermain selamat tanpa terkeluar ke sistem telefon."
                        else
                            "• ABC Phonics & 123 Numbers: Encourages speech imitation, counting, and phonological awareness.\n" +
                            "• Hijaiyah: Introduces Arabic alphabet and Fathah, Kasrah, Dammah vowels.\n" +
                            "• 7-Color Rainbow Snap: Sharpens visual discrimination with magnetic snapping.\n" +
                            "• Animals & Vehicles: Real sound effects foster imitation and recognition.\n" +
                            "• Curved Dot-to-Dot: Refines fine-motor pencil control without sharp jagged lines.\n" +
                            "• Toddler Screen Pinning: Guarantees your toddler cannot accidentally exit to the phone OS.",
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = Color(0xFF4A5568)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ToddlerButton(
                text = if (LanguageManager.isMalay()) "Kembali Bermain" else "Resume Playing",
                buttonColor = CandyCoral,
                shadowColor = CoralShadow,
                modifier = Modifier.fillMaxWidth(),
                height = 56.dp,
                onClick = onBack
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
