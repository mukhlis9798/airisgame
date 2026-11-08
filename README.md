# 🎈 Airis Game — Toddler & Preschool Early Learning App

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20(Material%203)-blue.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)](#)

**Airis Game** is a vibrant, interactive, bilingual early-learning application designed specifically for toddlers and preschoolers (ages 1 to 5). Built with **Jetpack Compose (Material 3)** and **Kotlin**, the app provides a safe, ad-free, and engaging digital playground combining touch gestures, colorful visuals, real-life photography, genuine animal and vehicle audio, and speech pronunciation.

---

## 🌟 Key Features & Activities

### 🔤 1. ABC Phonics & Vocabulary
- **Full A–Z Bilingual Learning**: Supports both **English** and **Bahasa Melayu** with real-time switching.
- **Real-World Photos**: Every letter features authentic, high-quality photography suited for toddler recognition:
  - **English**: Apple, Ball, Cat, Dog, Elephant, Fish, Giraffe, etc.
  - **Bahasa Melayu**: Epal, Bola, Cawan, Daun, Ikan, Zirafah, Kereta, etc.
- **Spoken Phonics & Word Audio**: Integrated text-to-speech engine speaks the letter name, phonetic sound, and corresponding vocabulary.

### 🕌 2. Hijaiyah (Arabic Alphabet)
- Complete Arabic alphabet cards (Alif to Ya) with correct phonetic pronunciations.
- Large, colorful toddler-friendly touch targets with gentle bouncing animations.

### 🦁 3. Animals & Sounds
- **20+ Interactive Animals**: Ape, Bee, Cat, Chicken, Cow, Dog, Duck, Elephant, Frog, Horse, Leopard, Lion, Owl, Penguin, Pig, Rooster, Sheep, Tiger, Wolf, Zebra.
- **Genuine Animal Sounds**: Real recorded animal cries and calls alongside spoken names in English and Malay.

### 🚗 4. Vehicles & Transportation
- **11 Transportation Modes**: Airplane, Ambulance, Bicycle, Boat, Bus, Car, Firetruck, Helicopter, Police Car, Rocket, Tractor, Train, Van.
- Authentic vehicle sound effects with high-resolution real photos.

### ✏️ 5. Connect-the-Dots (ABC & Lowercase Letters)
- **Smooth Catmull-Rom Splines**: Rounded letters (e.g. lowercase `a`, `b`, `c`, `o`) curve smoothly without sharp jagged lines.
- **Correct Geometric Sequencing**: Non-rounded letters (e.g. Capital `A`) follow standard stroke order without diagonal crossing slashes.
- Numbered/alphabetical guide points, glowing indicators for the next dot, and celebration confetti.

### 🎂 6. Connect-the-Dots (Objects & Toys)
- Kid-favorite silhouettes: Apple, Ice Cream Cone, Birthday Cake, Balloon, Teddy Bear, Flower, Car.
- Hybrid geometry: Curves for rounded tops (ice cream scoops, cake tiers) and straight lines for bases (cones, candles).

### 🎨 7. 7-Color Rainbow Snap Game
- **All 7 Rainbow Colors**: 🍎 Red (*Merah*), 🍊 Orange (*Oren*), ⭐ Yellow (*Kuning*), 🐸 Green (*Hijau*), 🐬 Blue (*Biru*), 🍇 Purple (*Ungu*), and 🌸 Pink (*Merah Jambu*).
- **Designed for Pre-Literate Toddlers**: Rich color-coded backgrounds, bold matching borders, and semi-transparent ghost silhouettes allow toddlers to recognize colors without reading.
- **3 Progressive Play Modes**:
  - ⭐ **Round 1 (3 Primary Colors)**: Red, Blue, Yellow — ideal for young toddlers.
  - 🌸 **Round 2 (4 Colors)**: Green, Orange, Purple, Pink.
  - 🌈 **Full Rainbow (7 Colors)**: All seven vibrant rainbow colors on screen!
- **Dual Input Modes**: Supports both fluid finger-dragging with magnetic snap physics AND instant tap-to-snap for tiny fingers.

### 🔷 8. Shapes & Geometry
- Circle, Square, Triangle, Star, Heart, Rectangle, Oval, and Diamond.
- Interactive animations and clear bilingual voiceovers.

### 🎹 9. Musical Xylophone
- 8-key colorful xylophone tuned to standard musical scale frequencies with real-time waveform sound generation and visual vibration pulses.

### 🎈 10. Balloon Pop & Sensory Play
- Endless floating colorful balloons with physics-based floating motion and snappy popping animations and audio.

### 🖍️ 11. Freehand Drawing Slate
- Simple toddler finger-painting canvas with quick color palette, brush sizes, and one-tap eraser.

### 🙈 12. Peekaboo Animal Surprise
- Tap-to-uncover interactive peekaboo play that delights younger toddlers with hidden animals.

### 🛡️ 13. Parent Gate & Settings
- **Child-Proof Gate**: Multi-step math challenge (e.g., `8 + 3 = ?`) prevents accidental settings access by little fingers.
- **Parental Controls**: Toggle background music, adjust sound effect volume, regulate speech rate, and switch default language.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Audio Engines**:
  - `SpeechEngine.kt`: Android native Text-To-Speech with English (`en_US`) and Malay (`ms_MY`) locale fallbacks.
  - `SoundEngine.kt`: Android `MediaPlayer` pool for high-fidelity MP3 sound effects and synthetic tone generation.
  - `AudioSettings.kt`: Persistent volume and sound preferences.
- **Language Management**: `LanguageManager.kt` supporting instant bilingual UI and audio switching.
- **Architecture**: Single Activity Compose Navigation architecture with modular, self-contained screen composables under `com.pocketdev.airisgame.ui.screens`.

---

## 📁 Project Structure

```
airisgame/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/pocketdev/airisgame/
│       │   ├── MainActivity.kt
│       │   ├── audio/
│       │   │   ├── AudioSettings.kt
│       │   │   ├── SoundEngine.kt
│       │   │   └── SpeechEngine.kt
│       │   ├── core/
│       │   │   └── LanguageManager.kt
│       │   └── ui/
│       │       ├── components/          # Headers, Buttons, Confetti, Parent Gate
│       │       ├── screens/             # All 12 toddler learning screens
│       │       └── theme/               # Playful color palette and typography
│       └── res/
│           ├── drawable/                # Optimized WebP real photos & vector icons
│           ├── raw/                     # Animal and vehicle sound effect MP3s
│           └── values/                  # Strings, colors, styles
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🚀 Building & Running

### Prerequisites
- Android SDK 35 (compileSdk: 35, minSdk: 26, targetSdk: 35)
- Java 17+ / OpenJDK 17
- Gradle 8.14+

### Build Debug APK
```bash
# Assemble the debug APK
./gradlew assembleDebug

# Output APK path:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 💖 Dedicated to Airis
*A lovingly crafted early childhood education app built to make learning playful, intuitive, and joyful!*
