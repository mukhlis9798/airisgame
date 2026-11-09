package com.pocketdev.airisgame.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object BedtimeTimerManager {
    private val scope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null

    // Options in minutes: 0 = Off, 15, 30, 45, 60
    var timerDurationMinutes by mutableStateOf(0)
        private set

    var remainingSeconds by mutableStateOf(0)
        private set

    var isBedtimeActive by mutableStateOf(false)

    fun setTimer(minutes: Int) {
        timerDurationMinutes = minutes
        timerJob?.cancel()
        if (minutes <= 0) {
            remainingSeconds = 0
            isBedtimeActive = false
            return
        }

        remainingSeconds = minutes * 60
        isBedtimeActive = false

        timerJob = scope.launch {
            while (isActive && remainingSeconds > 0) {
                delay(1000L)
                remainingSeconds--
            }
            if (remainingSeconds <= 0 && timerDurationMinutes > 0) {
                isBedtimeActive = true
            }
        }
    }

    fun extendTime(additionalMinutes: Int) {
        val newDuration = timerDurationMinutes + additionalMinutes
        timerDurationMinutes = newDuration
        remainingSeconds += additionalMinutes * 60
        isBedtimeActive = false

        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive && remainingSeconds > 0) {
                delay(1000L)
                remainingSeconds--
            }
            if (remainingSeconds <= 0 && timerDurationMinutes > 0) {
                isBedtimeActive = true
            }
        }
    }

    fun turnOff() {
        setTimer(0)
    }
}
