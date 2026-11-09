package com.pocketdev.airisgame.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val label: String, val shortCode: String) {
    EN("en", "English", "EN"),
    BM("ms", "B. Melayu", "BM")
}

object LanguageManager {
    var currentLanguage by mutableStateOf(AppLanguage.EN)

    fun toggleLanguage(): AppLanguage {
        currentLanguage = if (currentLanguage == AppLanguage.EN) AppLanguage.BM else AppLanguage.EN
        return currentLanguage
    }

    fun isMalay(): Boolean = currentLanguage == AppLanguage.BM

    // Home UI Strings
    fun appTitle(): String = "Airis World"
    fun appSubtitle(): String = if (isMalay()) "Bermain & Belajar" else "Toddler Play & Learn"
    fun greeting(): String = if (isMalay()) "Hai Airis! Jom main dan belajar!" else "Hello Airis! Let's play and learn!"

    // Activity Titles & Subtitles
    fun abcTitle(): String = if (isMalay()) "Huruf ABC" else "ABC Phonics"
    fun abcSubtitle(): String = if (isMalay()) "Kenal Huruf & Bunyi!" else "Phonics & Words!"

    fun numbersTitle(): String = if (isMalay()) "1 2 3 Nombor" else "1 2 3 Numbers"
    fun numbersSubtitle(): String = if (isMalay()) "Kira & Sentuh!" else "Count & Learn!"

    fun hijaiyahTitle(): String = "ا ب ت Hijaiyah"
    fun hijaiyahSubtitle(): String = if (isMalay()) "Belajar Baris Hijaiyah!" else "Arabic Letters & Harakat!"

    fun colorMatchTitle(): String = if (isMalay()) "Padanan Warna" else "Color Snap"
    fun colorMatchSubtitle(): String = if (isMalay()) "Tarik & Padankan!" else "Drag & Snap Colors!"

    fun animalsTitle(): String = if (isMalay()) "Haiwan Comel" else "Animal Safari"
    fun animalsSubtitle(): String = if (isMalay()) "Dengar & Sentuh!" else "Hear & Tap!"

    fun transportationTitle(): String = if (isMalay()) "Kenderaan" else "Vehicles"
    fun transportationSubtitle(): String = if (isMalay()) "Bunyi & Roda!" else "Sounds & Wheels!"

    fun abcDotsTitle(): String = if (isMalay()) "Sambung Titik ABC" else "Connect ABC Dots"
    fun abcDotsSubtitle(): String = if (isMalay()) "Huruf Besar & Kecil" else "Capital & Small Letters"

    fun thingsDotsTitle(): String = if (isMalay()) "Titik Comel" else "Connect Things"
    fun thingsDotsSubtitle(): String = if (isMalay()) "Kek, Aiskrim & Banyak Lagi!" else "Cake, Ice Cream & Fun!"

    fun balloonsTitle(): String = if (isMalay()) "Belon Ceria" else "Pop Balloons"
    fun balloonsSubtitle(): String = if (isMalay()) "1, 2, 3 Letup!" else "1, 2, 3 Pop!"

    fun shapesTitle(): String = if (isMalay()) "Bentuk & Warna" else "Shapes & Colors"
    fun shapesSubtitle(): String = if (isMalay()) "Padankan Bentuk!" else "Colors & Fit!"

    fun xylophoneTitle(): String = if (isMalay()) "Xilofon Ceria" else "Baby Xylophone"
    fun xylophoneSubtitle(): String = if (isMalay()) "Main Muzik!" else "Make Music!"

    fun peekabooTitle(): String = if (isMalay()) "Telur Kejutan" else "Surprise Eggs"
    fun peekabooSubtitle(): String = if (isMalay()) "Cak-Cak!" else "Peekaboo!"

    fun drawingTitle(): String = if (isMalay()) "Lukisan Ajaib" else "Magic Draw"
    fun drawingSubtitle(): String = if (isMalay()) "Warna-Warni!" else "Rainbows!"

    // Button & Audio Dialog Labels
    fun goBack(): String = if (isMalay()) "Kembali" else "Go back"
    fun findMe(): String = if (isMalay()) "Cari Saya" else "Find Me"
    fun finding(): String = if (isMalay()) "Mencari..." else "Finding!"
    fun songGuide(): String = if (isMalay()) "Panduan Lagu" else "Song Guide"
    fun again(): String = if (isMalay()) "Semula" else "Again"
    fun clear(): String = if (isMalay()) "Padam" else "Clear"

    // Sound Controls Dialog
    fun audioDialogTitle(): String = if (isMalay()) "Kawalan Audio" else "Sound & Volume"
    fun voiceVolumeLabel(): String = if (isMalay()) "Suara & Fonik" else "Voice & Phonics"
    fun voiceVolumeSub(): String = if (isMalay()) "Sebutan huruf, perkataan, dan xilofon" else "Spoken words, phonics, and xylophone"
    fun sfxVolumeLabel(): String = if (isMalay()) "Bunyi & Muzik" else "Sound Effects & Fun"
    fun sfxVolumeSub(): String = if (isMalay()) "Bunyi haiwan, kenderaan, dan letupan" else "Animals, vehicles, pops & cheers"
    fun testVoiceBtn(): String = if (isMalay()) "Uji Suara" else "Test Voice"
    fun testSfxBtn(): String = if (isMalay()) "Uji Bunyi" else "Test Sound"
    fun doneBtn(): String = if (isMalay()) "Selesai" else "Done"

    // Connect-the-Dots Options
    fun capitalLetters(): String = if (isMalay()) "Huruf Besar" else "Capital (A-Z)"
    fun smallLetters(): String = if (isMalay()) "Huruf Kecil" else "Small (a-z)"
}
