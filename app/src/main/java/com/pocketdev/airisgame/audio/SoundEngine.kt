package com.pocketdev.airisgame.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.SoundPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object SoundEngine {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private const val SAMPLE_RATE = 44100

    private var soundPool: SoundPool? = null
    private var appContext: Context? = null
    private val soundMap = mutableMapOf<Int, Int>()

    fun init(context: Context) {
        if (soundPool != null) return
        appContext = context.applicationContext
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            soundPool = SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build()
        } catch (_: Exception) {}
    }

    fun setMuted(muted: Boolean) {
        AudioSettings.isSfxMuted = muted
    }

    fun isMuted(): Boolean = AudioSettings.isSfxMuted

    /**
     * Plays a raw audio resource (e.g. real animal mp3 or vehicle mp3) from res/raw.
     */
    fun playRawSound(resId: Int) {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return

        val sp = soundPool
        val ctx = appContext
        if (sp != null && ctx != null) {
            val soundId = soundMap.getOrPut(resId) {
                sp.load(ctx, resId, 1)
            }
            val playResult = sp.play(soundId, volume, volume, 1, 0, 1.0f)
            if (playResult == 0) {
                // SoundPool might still be loading; retry after short delay
                scope.launch {
                    kotlinx.coroutines.delay(120L)
                    val v = AudioSettings.effectiveSfxVolume
                    if (v > 0.01f) {
                        sp.play(soundId, v, v, 1, 0, 1.0f)
                    }
                }
            }
        }
    }

    /**
     * Xylophone notes belong to the Voice & Learning Instrument category.
     */
    fun playXylophoneNote(frequency: Float) {
        val volume = AudioSettings.effectiveVoiceVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 0.75f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)
            val decay = 4.5f

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val envelope = exp(-decay * t)
                val fundamental = sin(2.0 * PI * frequency * t)
                val secondHarmonic = 0.35 * sin(2.0 * PI * frequency * 2.0 * t)
                val thirdHarmonic = 0.15 * sin(2.0 * PI * frequency * 3.0 * t)
                val sampleValue = (fundamental + secondHarmonic + thirdHarmonic) * envelope
                val scaled = (sampleValue * Short.MAX_VALUE * 0.7f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    fun playPop() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 0.08f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toFloat() / numSamples
                val currentFreq = 350.0 + 550.0 * progress
                val t = i.toFloat() / SAMPLE_RATE
                val envelope = exp(-8.0f * progress)
                val sampleValue = sin(2.0 * PI * currentFreq * t) * envelope
                val scaled = (sampleValue * Short.MAX_VALUE * 0.85f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    fun playCelebrationFanfare() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val notes = floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f)
            val noteDuration = 0.13f
            val totalDuration = noteDuration * notes.size + 0.35f
            val numSamples = (SAMPLE_RATE * totalDuration).toInt()
            val buffer = ShortArray(numSamples)

            for ((index, freq) in notes.withIndex()) {
                val startIndex = (index * noteDuration * SAMPLE_RATE).toInt()
                val noteLength = (SAMPLE_RATE * 0.45f).toInt()
                for (i in 0 until noteLength) {
                    val targetIdx = startIndex + i
                    if (targetIdx >= numSamples) break
                    val t = i.toFloat() / SAMPLE_RATE
                    val env = exp(-4.0f * t)
                    val sample = (sin(2.0 * PI * freq * t) + 0.25 * sin(4.0 * PI * freq * t)) * env
                    val cur = buffer[targetIdx].toInt()
                    val addition = (sample * Short.MAX_VALUE * 0.45f * volume).toInt()
                    buffer[targetIdx] = (cur + addition).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
            }
            playPcm(buffer)
        }
    }

    fun playBoing() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 0.3f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = exp(-4.0f * t)
                val mod = sin(2.0 * PI * 18.0 * t) * 60.0
                val freq = 190.0 + mod
                val sample = sin(2.0 * PI * freq * t) * env
                val scaled = (sample * Short.MAX_VALUE * 0.75f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    fun playWobble() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 0.22f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = exp(-4.5f * t)
                val freq = 240.0 + sin(2.0 * PI * 22.0 * t) * 35.0
                val sample = sin(2.0 * PI * freq * t) * env
                val scaled = (sample * Short.MAX_VALUE * 0.65f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    fun playCrack() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 0.05f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = exp(-25.0f * t)
                val noise = (Math.random() * 2.0 - 1.0) * 0.7
                val tone = sin(2.0 * PI * 1100.0 * t) * 0.3
                val sample = (noise + tone) * env
                val scaled = (sample * Short.MAX_VALUE * 0.8f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    fun playSparkle() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 0.18f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = exp(-7.0f * t)
                val freq1 = 1200.0 + (i.toDouble() / numSamples) * 600.0
                val freq2 = 2400.0
                val sample = (sin(2.0 * PI * freq1 * t) * 0.6 + sin(2.0 * PI * freq2 * t) * 0.4) * env
                val scaled = (sample * Short.MAX_VALUE * 0.6f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Procedural synthesis of Fire Truck Siren (Wailing American/European emergency siren)
     */
    fun playFireTruckSiren() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 1.2f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val sweep = (sin(2.0 * PI * 1.5 * t) + 1.0) * 0.5 // 0.0 to 1.0
                val freq = 550.0 + sweep * 450.0 // 550Hz to 1000Hz
                val sample = sin(2.0 * PI * freq * t)
                val scaled = (sample * Short.MAX_VALUE * 0.75f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Procedural synthesis of Ambulance Siren (Authentic two-tone Martin horn: Nee-Naw)
     */
    fun playAmbulanceSiren() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 1.1f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val cycle = (t * 2.8).toInt() % 2
                val freq = if (cycle == 0) 440.0 else 587.33
                val sample = sin(2.0 * PI * freq * t) + 0.2 * sin(4.0 * PI * freq * t)
                val scaled = (sample * Short.MAX_VALUE * 0.7f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Procedural synthesis of Airplane Jet Engine Roar
     */
    fun playAirplaneSound() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 1.2f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = if (t < 0.3f) t / 0.3f else if (t > 0.9f) (1.2f - t) / 0.3f else 1.0f
                val noise = (Math.random() * 2.0 - 1.0) * 0.65
                val hum = sin(2.0 * PI * 180.0 * t) * 0.35 + sin(2.0 * PI * 360.0 * t) * 0.2
                val sample = (noise + hum) * env
                val scaled = (sample * Short.MAX_VALUE * 0.65f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Procedural synthesis of Helicopter Rotor Chop-Chop
     */
    fun playHelicopterSound() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 1.2f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val pulse = (sin(2.0 * PI * 12.0 * t) + 1.0) * 0.5 // 12 chops per sec
                val chop = Math.pow(pulse, 4.0) // sharp pulse
                val noise = (Math.random() * 2.0 - 1.0) * 0.55
                val rumble = sin(2.0 * PI * 85.0 * t) * 0.45
                val sample = (noise + rumble) * chop
                val scaled = (sample * Short.MAX_VALUE * 0.8f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Procedural synthesis of Boat / Ship Foghorn
     */
    fun playBoatSound() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 1.0f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = if (t < 0.15f) t / 0.15f else if (t > 0.8f) (1.0f - t) / 0.2f else 1.0f
                val fund = sin(2.0 * PI * 130.0 * t)
                val h2 = sin(2.0 * PI * 260.0 * t) * 0.35
                val h3 = sin(2.0 * PI * 390.0 * t) * 0.2
                val sample = (fund + h2 + h3) * env
                val scaled = (sample * Short.MAX_VALUE * 0.75f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Procedural synthesis of Bus Air Horn
     */
    fun playBusSound() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 0.75f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = if (t < 0.08f) t / 0.08f else if (t > 0.6f) (0.75f - t) / 0.15f else 1.0f
                val tone1 = sin(2.0 * PI * 310.0 * t)
                val tone2 = sin(2.0 * PI * 370.0 * t) * 0.8
                val sample = (tone1 + tone2) * 0.5 * env
                val scaled = (sample * Short.MAX_VALUE * 0.8f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Procedural synthesis of Rocket Blastoff
     */
    fun playRocketSound() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 1.3f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = (t / 1.3f).coerceIn(0.2f, 1.0f)
                val noise = (Math.random() * 2.0 - 1.0) * 0.7
                val rumble = sin(2.0 * PI * (60.0 + t * 40.0) * t) * 0.4
                val sample = (noise + rumble) * env
                val scaled = (sample * Short.MAX_VALUE * 0.75f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Procedural synthesis of Tractor Diesel Chug
     */
    fun playTractorSound() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 1.1f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val stroke = (sin(2.0 * PI * 8.0 * t) + 1.0) * 0.5
                val chug = Math.pow(stroke, 3.0)
                val tone = sin(2.0 * PI * 110.0 * t) * 0.6
                val noise = (Math.random() * 2.0 - 1.0) * 0.4
                val sample = (tone + noise) * chug
                val scaled = (sample * Short.MAX_VALUE * 0.8f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val minBuf = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val trackSize = maxOf(buffer.size * 2, minBuf)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(trackSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()

            val durationMs = ((buffer.size.toFloat() / SAMPLE_RATE) * 1000).toLong() + 150
            scope.launch {
                kotlinx.coroutines.delay(durationMs)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // AudioTrack fallback
        }
    }
}
