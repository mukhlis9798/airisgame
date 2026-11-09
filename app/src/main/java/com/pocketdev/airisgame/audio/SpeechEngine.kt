package com.pocketdev.airisgame.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pocketdev.airisgame.core.LanguageManager
import java.util.Locale

object SpeechEngine {
    private var tts: TextToSpeech? = null
    var isInitialized by mutableStateOf(false)
        private set

    var isMalayTtsAvailable by mutableStateOf(false)
        private set

    var isArabicTtsAvailable by mutableStateOf(false)
        private set

    var pitch: Float = 1.25f
        set(value) {
            field = value
            tts?.setPitch(value)
        }

    var speechRate: Float = 0.85f
        set(value) {
            field = value
            tts?.setSpeechRate(value)
        }

    fun init(context: Context) {
        if (tts != null) return
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                tts?.let { engine ->
                    verifyLocales(engine)
                    applyLanguage(engine)
                    engine.setPitch(pitch)
                    engine.setSpeechRate(speechRate)
                }
            }
        }
    }

    private fun verifyLocales(engine: TextToSpeech) {
        try {
            val msLocale = Locale("ms", "MY")
            val msStatus = engine.isLanguageAvailable(msLocale)
            isMalayTtsAvailable = (msStatus >= TextToSpeech.LANG_AVAILABLE)

            val arLocale = Locale("ar")
            val arStatus = engine.isLanguageAvailable(arLocale)
            isArabicTtsAvailable = (arStatus >= TextToSpeech.LANG_AVAILABLE)
        } catch (_: Exception) {
            isMalayTtsAvailable = false
            isArabicTtsAvailable = false
        }
    }

    /**
     * Launch system settings intent to download missing TTS voice data packages
     */
    fun openInstallTtsDataIntent(context: Context) {
        try {
            val installIntent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(installIntent)
        } catch (_: Exception) {
            // Intent not handled by custom OEM ROM
        }
    }

    private fun applyLanguage(engine: TextToSpeech) {
        val targetLocale = if (LanguageManager.isMalay()) {
            val msLocale = Locale("ms", "MY")
            val avail = engine.isLanguageAvailable(msLocale)
            if (avail >= TextToSpeech.LANG_AVAILABLE) {
                msLocale
            } else {
                val idLocale = Locale("id", "ID")
                if (engine.isLanguageAvailable(idLocale) >= TextToSpeech.LANG_AVAILABLE) idLocale else Locale.getDefault()
            }
        } else {
            Locale.US
        }
        try {
            engine.setLanguage(targetLocale)
        } catch (_: Exception) {}
    }

    fun setMuted(muted: Boolean) {
        AudioSettings.isVoiceMuted = muted
        if (muted) {
            stop()
        }
    }

    fun isMuted(): Boolean = AudioSettings.isVoiceMuted

    fun speak(text: String, flush: Boolean = true) {
        val volume = AudioSettings.effectiveVoiceVolume
        if (volume <= 0.01f || !isInitialized) return
        try {
            tts?.let { engine ->
                applyLanguage(engine)
                val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                val params = Bundle().apply {
                    putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume)
                }
                engine.speak(text, queueMode, params, "AIRIS_TTS_${System.currentTimeMillis()}")
            }
        } catch (_: Exception) {}
    }

    /**
     * Speaks Arabic phonetics authentically.
     * If Arabic TTS data is installed, speaks arabicText directly with native Makhraj.
     * Otherwise, uses phoneticFallback so it never spells out letters!
     */
    fun speakArabic(arabicText: String, phoneticFallback: String) {
        val volume = AudioSettings.effectiveVoiceVolume
        if (volume <= 0.01f || !isInitialized) return
        try {
            tts?.let { engine ->
                val arLocale = Locale("ar")
                val avail = engine.isLanguageAvailable(arLocale)
                val params = Bundle().apply {
                    putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume)
                }
                if (avail >= TextToSpeech.LANG_AVAILABLE) {
                    engine.setLanguage(arLocale)
                    engine.speak(arabicText, TextToSpeech.QUEUE_FLUSH, params, "AR_TTS_${System.currentTimeMillis()}")
                } else {
                    applyLanguage(engine)
                    engine.speak(phoneticFallback, TextToSpeech.QUEUE_FLUSH, params, "AR_FALLBACK_${System.currentTimeMillis()}")
                }
            }
        } catch (_: Exception) {}
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (_: Exception) {}
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (_: Exception) {}
    }
}
