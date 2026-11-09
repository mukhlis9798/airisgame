package com.pocketdev.airisgame.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Global Screen Pinning state manager connecting MainActivity LockTask API
 * with ToddlerLockIndicator inside ToddlerAppHeader across all screens.
 */
object ScreenPinningManager {
    var isPinned by mutableStateOf(false)

    var onLockRequested: (() -> Unit)? = null
    var onUnlockConfirmed: (() -> Unit)? = null

    fun lock() {
        onLockRequested?.invoke()
    }

    fun unlock() {
        onUnlockConfirmed?.invoke()
    }
}
