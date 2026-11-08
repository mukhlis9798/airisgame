package com.pocketdev.airisgame.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.pocketdev.airisgame.core.LanguageManager
import java.util.Locale

object SpeechEngine {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

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
                    applyLanguage(engine)
                    engine.setPitch(pitch)
                    engine.setSpeechRate(speechRate)
                }
            }
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
        engine.setLanguage(targetLocale)
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
     * Otherwise, uses phoneticFallback (e.g. "Thaa", "Ba", "Bi", "Bu") so it never spells out letters!
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
                    // Fallback to tuned phonetic string with standard engine
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
