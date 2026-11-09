package com.pocketdev.airisgame.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.SoundPool
import com.pocketdev.airisgame.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object SoundEngine {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private const val SAMPLE_RATE = 44100
    private const val MAX_STREAMS = 10

    private var soundPool: SoundPool? = null
    private var appContext: Context? = null
    private val soundMap = ConcurrentHashMap<Int, Int>()

    // All bundled raw audio resources to preload for sub-15ms latency
    private val rawResourceList = listOf(
        R.raw.sound_animal_ape,
        R.raw.sound_animal_bee,
        R.raw.sound_animal_cat,
        R.raw.sound_animal_chicken,
        R.raw.sound_animal_cow,
        R.raw.sound_animal_dog,
        R.raw.sound_animal_duck,
        R.raw.sound_animal_elephant,
        R.raw.sound_animal_frog,
        R.raw.sound_animal_horse,
        R.raw.sound_animal_leopard,
        R.raw.sound_animal_lion,
        R.raw.sound_animal_owl,
        R.raw.sound_animal_penguin,
        R.raw.sound_animal_pig,
        R.raw.sound_animal_rooster,
        R.raw.sound_animal_sheep,
        R.raw.sound_animal_tiger,
        R.raw.sound_animal_wolf,
        R.raw.sound_animal_zebra,
        R.raw.sound_vehicle_bicycle,
        R.raw.sound_vehicle_car,
        R.raw.sound_vehicle_police,
        R.raw.sound_vehicle_train
    )

    fun init(context: Context) {
        if (soundPool != null) return
        appContext = context.applicationContext
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val sp = SoundPool.Builder()
                .setMaxStreams(MAX_STREAMS)
                .setAudioAttributes(audioAttributes)
                .build()
            soundPool = sp

            // Preload all raw sound resources in background thread
            scope.launch(Dispatchers.IO) {
                val ctx = appContext ?: return@launch
                for (resId in rawResourceList) {
                    try {
                        val soundId = sp.load(ctx, resId, 1)
                        soundMap[resId] = soundId
                    } catch (_: Exception) {}
                }
            }
        } catch (_: Exception) {}
    }

    /**
     * Pause all active sound streams when Activity pauses
     */
    fun pauseAll() {
        try {
            soundPool?.autoPause()
        } catch (_: Exception) {}
    }

    /**
     * Resume sound streams when Activity resumes
     */
    fun resumeAll() {
        try {
            soundPool?.autoResume()
        } catch (_: Exception) {}
    }

    /**
     * Release all SoundPool resources on Activity destroy
     */
    fun release() {
        try {
            soundPool?.release()
            soundPool = null
            soundMap.clear()
        } catch (_: Exception) {}
    }

    fun setMuted(muted: Boolean) {
        AudioSettings.isSfxMuted = muted
    }

    fun isMuted(): Boolean = AudioSettings.isSfxMuted

    /**
     * Plays a raw audio resource with concurrent polyphony and low latency.
     */
    fun playRawSound(resId: Int) {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return

        val sp = soundPool ?: return
        val ctx = appContext ?: return

        val soundId = soundMap[resId]
        if (soundId != null) {
            val playResult = sp.play(soundId, volume, volume, 1, 0, 1.0f)
            if (playResult == 0) {
                // If stream failed because sound was still loading, retry briefly
                scope.launch {
                    kotlinx.coroutines.delay(100L)
                    val v = AudioSettings.effectiveSfxVolume
                    if (v > 0.01f) {
                        sp.play(soundId, v, v, 1, 0, 1.0f)
                    }
                }
            }
        } else {
            // Lazy load and play
            val newSoundId = sp.load(ctx, resId, 1)
            soundMap[resId] = newSoundId
            sp.setOnLoadCompleteListener { pool, sampleId, status ->
                if (status == 0 && sampleId == newSoundId) {
                    val v = AudioSettings.effectiveSfxVolume
                    if (v > 0.01f) {
                        pool.play(newSoundId, v, v, 1, 0, 1.0f)
                    }
                }
            }
        }
    }

    /**
     * Procedural Xylophone note synthesis with rich overtone harmonics.
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

    fun playAirplaneSound() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 1.0f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = exp(-1.8f * t)
                val noise = (Math.random() * 2.0 - 1.0) * 0.8
                val whoosh = noise * env
                val scaled = (whoosh * Short.MAX_VALUE * 0.7f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    fun playBoatSound() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 0.9f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = exp(-1.5f * t)
                val horn = sin(2.0 * PI * 185.0 * t) * 0.6 + sin(2.0 * PI * 220.0 * t) * 0.4
                val sample = horn * env
                val scaled = (sample * Short.MAX_VALUE * 0.8f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    fun playHelicopterSound() = playHelicopterRotor()
    fun playBusSound() = playBusHorn()
    fun playRocketSound() = playRocketThrust()
    fun playTractorSound() = playTractorEngine()

    fun playFireTruckSiren() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 1.2f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val sweep = (sin(2.0 * PI * 1.5 * t) + 1.0) * 0.5
                val freq = 550.0 + sweep * 450.0
                val sample = sin(2.0 * PI * freq * t)
                val scaled = (sample * Short.MAX_VALUE * 0.75f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

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

    fun playBicycleBell() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 0.6f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val ring1 = exp(-7.0f * t) * sin(2.0 * PI * 2093.0 * t)
                val ring2 = exp(-9.0f * t) * sin(2.0 * PI * 2637.0 * t) * 0.6
                val secondChimeOffset = 0.12f
                val ring3 = if (t > secondChimeOffset) {
                    val t2 = t - secondChimeOffset
                    exp(-7.0f * t2) * sin(2.0 * PI * 2093.0 * t2) * 0.9
                } else 0.0

                val sample = ring1 + ring2 + ring3
                val scaled = (sample * Short.MAX_VALUE * 0.6f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    fun playHelicopterRotor() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 1.2f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val bladeChop = sin(2.0 * PI * 14.0 * t).coerceAtLeast(0.0)
                val chopperNoise = (bladeChop * bladeChop) * (sin(2.0 * PI * 110.0 * t) + (Math.random() * 0.3))
                val scaled = (chopperNoise * Short.MAX_VALUE * 0.8f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    fun playRocketThrust() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 1.3f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val envelope = if (t < 0.2f) (t / 0.2f) else exp(-1.5f * (t - 0.2f))
                val rumble = (Math.random() * 2.0 - 1.0) * 0.7 + sin(2.0 * PI * 65.0 * t) * 0.3
                val sample = rumble * envelope
                val scaled = (sample * Short.MAX_VALUE * 0.7f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    fun playTractorEngine() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 1.2f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val chug = sin(2.0 * PI * 8.0 * t).coerceAtLeast(0.0)
                val engine = (chug * chug) * sin(2.0 * PI * 85.0 * t)
                val scaled = (engine * Short.MAX_VALUE * 0.85f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    fun playBusHorn() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            val durationSec = 0.5f
            val numSamples = (SAMPLE_RATE * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val env = exp(-2.5f * t)
                val hornTone = sin(2.0 * PI * 311.13 * t) * 0.6 + sin(2.0 * PI * 370.0 * t) * 0.4
                val sample = hornTone * env
                val scaled = (sample * Short.MAX_VALUE * 0.85f * volume).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = scaled.toShort()
            }
            playPcm(buffer)
        }
    }

    fun playLullabyMelody() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        scope.launch {
            // Gentle Twinkle Twinkle lullaby notes (C4, C4, G4, G4, A4, A4, G4)
            val notes = floatArrayOf(261.63f, 261.63f, 392.00f, 392.00f, 440.00f, 440.00f, 392.00f)
            val noteDuration = 0.35f
            val totalDuration = noteDuration * notes.size + 0.5f
            val numSamples = (SAMPLE_RATE * totalDuration).toInt()
            val buffer = ShortArray(numSamples)

            for ((index, freq) in notes.withIndex()) {
                val startIndex = (index * noteDuration * SAMPLE_RATE).toInt()
                val noteLength = (SAMPLE_RATE * 0.5f).toInt()
                for (i in 0 until noteLength) {
                    val targetIdx = startIndex + i
                    if (targetIdx >= numSamples) break
                    val t = i.toFloat() / SAMPLE_RATE
                    val env = exp(-3.0f * t)
                    val sample = sin(2.0 * PI * freq * t) * env
                    val cur = buffer[targetIdx].toInt()
                    val addition = (sample * Short.MAX_VALUE * 0.4f * volume).toInt()
                    buffer[targetIdx] = (cur + addition).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
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
