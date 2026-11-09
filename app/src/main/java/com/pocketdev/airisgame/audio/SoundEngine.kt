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
    private val xyloSoundMap = ConcurrentHashMap<Int, Int>()
    private var popSoundId: Int = 0
    private var clickSoundId: Int = 0
    private var carHonkSoundId: Int = 0
    private var chimeSoundId: Int = 0

    // Standard xylophone frequencies: C5, D5, E5, F5, G5, A5, B5, C6
    private val xyloFreqs = intArrayOf(261, 293, 329, 349, 392, 440, 493, 523)

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

            // Preload all raw sound resources and synthesized short sounds in background thread
            scope.launch(Dispatchers.IO) {
                val ctx = appContext ?: return@launch
                for (resId in rawResourceList) {
                    try {
                        val soundId = sp.load(ctx, resId, 1)
                        soundMap[resId] = soundId
                    } catch (_: Exception) {}
                }

                // Pre-generate and load short sound effects into SoundPool for sub-15ms latency
                try {
                    val soundDir = java.io.File(ctx.cacheDir, "sounds").apply { mkdirs() }

                    // 1. Balloon Pop
                    val popFile = java.io.File(soundDir, "pop.wav")
                    if (!popFile.exists()) {
                        writeWavFile(popFile, generatePopSamples())
                    }
                    popSoundId = sp.load(popFile.absolutePath, 1)

                    // 2. Click
                    val clickFile = java.io.File(soundDir, "click.wav")
                    if (!clickFile.exists()) {
                        writeWavFile(clickFile, generateClickSamples())
                    }
                    clickSoundId = sp.load(clickFile.absolutePath, 1)

                    // 3. Car Honk
                    val carFile = java.io.File(soundDir, "car_honk.wav")
                    if (!carFile.exists()) {
                        writeWavFile(carFile, generateCarHonkSamples())
                    }
                    carHonkSoundId = sp.load(carFile.absolutePath, 1)

                    // 4. Chime
                    val chimeFile = java.io.File(soundDir, "chime.wav")
                    if (!chimeFile.exists()) {
                        writeWavFile(chimeFile, generateChimeSamples())
                    }
                    chimeSoundId = sp.load(chimeFile.absolutePath, 1)

                    // 5. Xylophone notes
                    for (freq in xyloFreqs) {
                        val noteFile = java.io.File(soundDir, "xylo_$freq.wav")
                        if (!noteFile.exists()) {
                            writeWavFile(noteFile, generateXyloSamples(freq.toFloat()))
                        }
                        val sid = sp.load(noteFile.absolutePath, 1)
                        xyloSoundMap[freq] = sid
                    }
                } catch (_: Exception) {}
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
     * Procedural and preloaded SoundPool Xylophone note synthesis with sub-15ms latency.
     */
    fun playXylophoneNote(frequency: Float) {
        val volume = AudioSettings.effectiveVoiceVolume
        if (volume <= 0.01f) return
        val sp = soundPool
        val closestFreq = xyloFreqs.minByOrNull { kotlin.math.abs(it - frequency.toInt()) }
        val soundId = if (closestFreq != null) xyloSoundMap[closestFreq] else null
        if (sp != null && soundId != null && soundId != 0) {
            val rate = (frequency / closestFreq!!.toFloat()).coerceIn(0.5f, 2.0f)
            val res = sp.play(soundId, volume, volume, 2, 0, rate)
            if (res != 0) return
        }

        // Procedural fallback
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
        val sp = soundPool
        if (sp != null && popSoundId != 0) {
            val res = sp.play(popSoundId, volume, volume, 1, 0, 1.0f)
            if (res != 0) return
        }

        // Procedural fallback
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

    fun playClick() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        val sp = soundPool
        if (sp != null && clickSoundId != 0) {
            val res = sp.play(clickSoundId, volume, volume, 1, 0, 1.0f)
            if (res != 0) return
        }
        playPop()
    }

    fun playCarHonk() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        val sp = soundPool
        if (sp != null && carHonkSoundId != 0) {
            val res = sp.play(carHonkSoundId, volume, volume, 1, 0, 1.0f)
            if (res != 0) return
        }
        playBusHorn()
    }

    fun playChime() {
        val volume = AudioSettings.effectiveSfxVolume
        if (volume <= 0.01f) return
        val sp = soundPool
        if (sp != null && chimeSoundId != 0) {
            val res = sp.play(chimeSoundId, volume, volume, 1, 0, 1.0f)
            if (res != 0) return
        }
        playSparkle()
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

    private fun generatePopSamples(): ShortArray {
        val durationSec = 0.08f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val progress = i.toFloat() / numSamples
            val currentFreq = 350.0 + 550.0 * progress
            val t = i.toFloat() / SAMPLE_RATE
            val envelope = exp(-8.0f * progress)
            val sampleValue = sin(2.0 * PI * currentFreq * t) * envelope
            val scaled = (sampleValue * Short.MAX_VALUE * 0.9f).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            buffer[i] = scaled.toShort()
        }
        return buffer
    }

    private fun generateClickSamples(): ShortArray {
        val durationSec = 0.04f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toFloat() / SAMPLE_RATE
            val envelope = exp(-30.0f * (i.toFloat() / numSamples))
            val sampleValue = sin(2.0 * PI * 1200.0 * t) * envelope
            val scaled = (sampleValue * Short.MAX_VALUE * 0.8f).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            buffer[i] = scaled.toShort()
        }
        return buffer
    }

    private fun generateCarHonkSamples(): ShortArray {
        val toneDuration = 0.12f
        val gapDuration = 0.04f
        val totalDuration = toneDuration * 2 + gapDuration
        val numSamples = (SAMPLE_RATE * totalDuration).toInt()
        val buffer = ShortArray(numSamples)
        val toneSamples = (SAMPLE_RATE * toneDuration).toInt()
        val gapSamples = (SAMPLE_RATE * gapDuration).toInt()

        for (tone in 0..1) {
            val start = tone * (toneSamples + gapSamples)
            for (i in 0 until toneSamples) {
                val idx = start + i
                if (idx >= numSamples) break
                val t = i.toFloat() / SAMPLE_RATE
                val env = (1.0f - exp(-15.0f * (i.toFloat() / toneSamples))) * (1.0f - (i.toFloat() / toneSamples) * 0.2f)
                val sample = (sin(2.0 * PI * 520.0 * t) + 0.6 * sin(2.0 * PI * 650.0 * t)) * env
                val scaled = (sample * Short.MAX_VALUE * 0.5f).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[idx] = scaled.toShort()
            }
        }
        return buffer
    }

    private fun generateChimeSamples(): ShortArray {
        val notes = floatArrayOf(1046.50f, 1318.51f, 1567.98f, 2093.00f)
        val noteDur = 0.08f
        val totalDur = noteDur * notes.size + 0.35f
        val numSamples = (SAMPLE_RATE * totalDur).toInt()
        val buffer = ShortArray(numSamples)

        for ((index, freq) in notes.withIndex()) {
            val startIndex = (index * noteDur * SAMPLE_RATE).toInt()
            val noteLength = (SAMPLE_RATE * 0.45f).toInt()
            for (i in 0 until noteLength) {
                val targetIdx = startIndex + i
                if (targetIdx >= numSamples) break
                val t = i.toFloat() / SAMPLE_RATE
                val env = exp(-7.0f * t)
                val sample = sin(2.0 * PI * freq * t) * env
                val cur = buffer[targetIdx].toInt()
                val addition = (sample * Short.MAX_VALUE * 0.4f).toInt()
                buffer[targetIdx] = (cur + addition).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
        }
        return buffer
    }

    private fun generateXyloSamples(frequency: Float): ShortArray {
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
            val scaled = (sampleValue * Short.MAX_VALUE * 0.75f).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            buffer[i] = scaled.toShort()
        }
        return buffer
    }

    private fun writeWavFile(file: java.io.File, samples: ShortArray, sampleRate: Int = SAMPLE_RATE) {
        val dataSize = samples.size * 2
        val totalSize = dataSize + 36
        java.io.FileOutputStream(file).use { out ->
            val header = ByteArray(44)
            header[0] = 'R'.code.toByte()
            header[1] = 'I'.code.toByte()
            header[2] = 'F'.code.toByte()
            header[3] = 'F'.code.toByte()
            header[4] = (totalSize and 0xff).toByte()
            header[5] = ((totalSize shr 8) and 0xff).toByte()
            header[6] = ((totalSize shr 16) and 0xff).toByte()
            header[7] = ((totalSize shr 24) and 0xff).toByte()
            header[8] = 'W'.code.toByte()
            header[9] = 'A'.code.toByte()
            header[10] = 'V'.code.toByte()
            header[11] = 'E'.code.toByte()
            header[12] = 'f'.code.toByte()
            header[13] = 'm'.code.toByte()
            header[14] = 't'.code.toByte()
            header[15] = ' '.code.toByte()
            header[16] = 16
            header[17] = 0
            header[18] = 0
            header[19] = 0
            header[20] = 1 // Audio format 1 = PCM
            header[21] = 0
            header[22] = 1 // 1 channel
            header[23] = 0
            val byteRate = sampleRate * 2
            header[24] = (sampleRate and 0xff).toByte()
            header[25] = ((sampleRate shr 8) and 0xff).toByte()
            header[26] = ((sampleRate shr 16) and 0xff).toByte()
            header[27] = ((sampleRate shr 24) and 0xff).toByte()
            header[28] = (byteRate and 0xff).toByte()
            header[29] = ((byteRate shr 8) and 0xff).toByte()
            header[30] = ((byteRate shr 16) and 0xff).toByte()
            header[31] = ((byteRate shr 24) and 0xff).toByte()
            header[32] = 2 // Block align
            header[33] = 0
            header[34] = 16 // Bits per sample
            header[35] = 0
            header[36] = 'd'.code.toByte()
            header[37] = 'a'.code.toByte()
            header[38] = 't'.code.toByte()
            header[39] = 'a'.code.toByte()
            header[40] = (dataSize and 0xff).toByte()
            header[41] = ((dataSize shr 8) and 0xff).toByte()
            header[42] = ((dataSize shr 16) and 0xff).toByte()
            header[43] = ((dataSize shr 24) and 0xff).toByte()
            out.write(header)

            val byteBuf = java.nio.ByteBuffer.allocate(dataSize).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            for (s in samples) {
                byteBuf.putShort(s)
            }
            out.write(byteBuf.array())
        }
    }
}
