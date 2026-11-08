package com.pocketdev.airisgame.audio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Global audio state separating Voice & Learning (Phonics, Letters, Xylophone)
 * from Sound Effects & Fun (Animals, Vehicles, Pops, Celebrations).
 */
object AudioSettings {
    var voiceVolume by mutableStateOf(1.0f)
    var isVoiceMuted by mutableStateOf(false)

    var sfxVolume by mutableStateOf(0.85f)
    var isSfxMuted by mutableStateOf(false)

    val effectiveVoiceVolume: Float
        get() = if (isVoiceMuted) 0.0f else voiceVolume.coerceIn(0.0f, 1.0f)

    val effectiveSfxVolume: Float
        get() = if (isSfxMuted) 0.0f else sfxVolume.coerceIn(0.0f, 1.0f)

    fun toggleVoiceMute(): Boolean {
        isVoiceMuted = !isVoiceMuted
        return isVoiceMuted
    }

    fun toggleSfxMute(): Boolean {
        isSfxMuted = !isSfxMuted
        return isSfxMuted
    }

    val isAllMuted: Boolean
        get() = (isVoiceMuted || voiceVolume <= 0.01f) && (isSfxMuted || sfxVolume <= 0.01f)
}
