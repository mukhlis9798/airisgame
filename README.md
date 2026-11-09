# 🎈 Airis Game — Toddler & Preschool Early Learning App

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20(Material%203)-blue.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)](#)

**Airis Game** is a vibrant, interactive, bilingual early-learning application designed specifically for toddlers and preschoolers (ages 1 to 5). Built with **Jetpack Compose (Material 3)** and **Kotlin**, the app provides a safe, ad-free, and engaging digital playground combining touch gestures, colorful visuals, real-life photography, low-latency audio, native screen pinning locks, and spoken pronunciation in English and Bahasa Melayu.

---

## 🌟 Key Features & Activities

### 🔤 1. ABC Phonics & Vocabulary
- **Full A–Z Bilingual Learning**: Supports both **English** and **Bahasa Melayu** with real-time switching.
- **Real-World Photos**: Every letter features authentic photography suited for toddler recognition:
  - **English**: Apple, Ball, Cat, Dog, Elephant, Fish, Giraffe, etc.
  - **Bahasa Melayu**: Epal, Bola, Cawan, Daun, Ikan, Zirafah, Kereta, etc.
- **Spoken Phonics & Word Audio**: Integrated text-to-speech engine speaks the letter name, phonetic sound, and corresponding vocabulary.

### 🔢 2. 123 Numbers & Counting (1 to 10)
- **Interactive Tap-to-Count**: Toddlers tap each item (1 Sun, 2 Cats, 3 Apples, 4 Cars, 5 Stars, 6 Fish, 7 Balloons, 8 Flowers, 9 Bananas, 10 Bees).
- **Audio Feedback**: Speaks numbers in order ("Satu", "Dua", "Tiga" / "One", "Two", "Three") with celebratory fanfare and confetti upon completing each count.
- **Tactile Haptics**: Subtle tactile clicks on each tapped item reinforce motor-skill development.

### 🕌 3. Hijaiyah (Arabic Alphabet)
- Complete Arabic alphabet cards (Alif to Ya) with correct phonetic pronunciations.
- Large, colorful toddler-friendly touch targets with gentle bouncing animations.

### 🦁 4. Animals & Sounds
- **20+ Interactive Animals**: Ape, Bee, Cat, Chicken, Cow, Dog, Duck, Elephant, Frog, Horse, Leopard, Lion, Owl, Penguin, Pig, Rooster, Sheep, Tiger, Wolf, Zebra.
- **Preloaded Low-Latency Audio**: Built with Android's native `SoundPool` for sub-15ms instant animal cries without playback delays.

### 🚗 5. Vehicles & Transportation
- **11 Transportation Modes**: Airplane, Ambulance, Bicycle, Boat, Bus, Car, Firetruck, Helicopter, Police Car, Rocket, Tractor, Train, Van.
- Authentic vehicle sound effects and emergency sirens with real photos.

### ✏️ 6. ABC Tracing (Authentic Typography Stencil to Solid Strokes)
- **Authentic Educational Sans-Serif Typography**: Replaced abstract dot polygons with standard educational sans-serif letterforms across all 26 letters (A–Z) in both Capital and Small cases.
- **Faint Letter Stencil Background**: Renders a subtle semi-transparent letter stencil background (`alpha = 0.12f`) so toddlers immediately recognize authentic letterforms.
- **Dashed Outlines to Solid Strokes**: Initial untraced letter displays as a prominent dashed guide (`20f, 15f`). Freeform tracing with generous touch tolerance (~38.dp hit-box) converts dashed segments into thick, vibrant solid strokes (~24.dp).
- **Single-Pointer Palm Rejection**: Ignores resting palms, wrists, and stray fingers while tracing.
- **Audio & Visual Celebrations**: Plays positive sparkle feedback, triggers gentle haptic ticks on stroke completion, and launches fanfare and confetti upon completing the letter.

### 🎨 7. Cartoon Silhouettes Tracing (Flower, Car, Teddy, Balloon, Cake)
- **Distinct Cartoon Line-Art Paths**: Replaced generic loops of dots with instantly recognizable cartoon silhouettes:
  - 🌸 **Flower**: Center circle, 5 distinct rounded petals, stem, and leaves.
  - 🚗 **Cute Car**: Aerodynamic car body silhouette with wheels, headlights, and windows.
  - 🧸 **Teddy Bear**: Distinct head with circular ears, snout, body, arms, and feet.
  - 🎈 **Floating Balloon**: Oval balloon body with knot and squiggly trailing string.
  - 🎂 **Birthday Cake**: Tiered cake silhouette with decorative candles and flames.
- **Playful Dashed Outlines to Solid Lines**: Freeform tracing converts dashed outlines into vibrant solid colored lines.
- **Spring Bounce Illustration Reveal**: When the full silhouette is traced, a bouncy spring transition reveals the fully colored cartoon illustration, accompanied by matching sound effects (e.g., car honk, chime, balloon pop, fanfare), haptics, and celebration confetti.

### 🎨 8. 7-Color Rainbow Snap Game
- **All 7 Rainbow Colors**: 🍎 Red (*Merah*), 🍊 Orange (*Oren*), ⭐ Yellow (*Kuning*), 🐸 Green (*Hijau*), 🐬 Blue (*Biru*), 🍇 Purple (*Ungu*), and 🌸 Pink (*Merah Jambu*).
- **Designed for Pre-Literate Toddlers**: Rich color-coded backgrounds, bold matching borders, and semi-transparent ghost silhouettes allow toddlers to recognize colors without reading.
- **3 Progressive Play Modes**:
  - ⭐ **Round 1 (3 Primary Colors)**: Red, Blue, Yellow — ideal for young toddlers.
  - 🌸 **Round 2 (4 Colors)**: Green, Orange, Purple, Pink.
  - 🌈 **Full Rainbow (7 Colors)**: All seven vibrant rainbow colors on screen!
- **Dual Input Modes**: Supports both fluid finger-dragging with magnetic snap physics AND instant tap-to-snap for tiny fingers.

### 🔷 9. Shapes & Geometry
- Circle, Square, Triangle, Star, Heart, Rectangle, Oval, and Diamond.
- Interactive animations and clear bilingual voiceovers.

### 🎹 10. Musical Xylophone
- 8-key colorful xylophone tuned to standard musical scale frequencies with real-time waveform sound generation and visual vibration pulses.

### 🎈 11. Balloon Pop & Sensory Play
- Endless floating colorful balloons with physics-based floating motion, sharp haptic burst pulses, and snappy popping animations.

### 🖍️ 12. Ultra-Fast Freehand Drawing Slate
- **High-Performance Bitmap Caching**: Maintains an off-screen mutable `Bitmap` and native `Canvas` to render strokes at 60/120fps with zero Compose recomposition bottlenecks.
- **Palm Rejection**: Discards extra touches when toddlers rest their hands on the screen.
- **Brush Selector**: Fine, Medium, and Thick stroke sizes, glowing rainbow mode, and one-tap clean slate.

### 🙈 13. Peekaboo Animal Surprise
- Tap-to-uncover interactive peekaboo play that delights younger toddlers with hidden animals.

---

## 🛡️ Toddler Safety & Parental Controls

### 🔒 Native Screen Pinning & Lock Task Mode (`startLockTask`)
- **Toddler Pin Guard**: Enables Android's native Lock Task mode and sticky immersive mode, hiding the status bar and trapping the Home/Back/Recents navigation gestures.
- **Parent Long-Press Unlock**: A subtle, non-distracting padlock indicator requires an uninterrupted 3-second hold with a filling progress ring, followed by a Math Challenge Gate ("7 + 5 = ?") before unlocking.

### 🌙 Bedtime & Screen Time Timer
- Gentle session duration limits (Off, 15m, 30m, 45m, 60m).
- When time elapses, displays a soothing full-screen nighttime transition with starry skies, sleeping animations, and a lullaby rather than abruptly terminating the app.

### 🎙️ Offline Malay Voice Pack Diagnostics
- Diagnostic verification for Malaysian Malay (`ms_MY`) offline TTS voice data with direct shortcut to install official Google voice data packs.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Audio Engines**:
  - `SoundEngine.kt`: Android native `SoundPool` with 10 concurrent streams and raw resource preloading for sub-15ms latency.
  - `SpeechEngine.kt`: Android native Text-To-Speech with English (`en_US`) and Malay (`ms_MY`) locale fallbacks.
  - `AudioSettings.kt`: Persistent volume and sound preferences.
  - `BedtimeTimerManager.kt`: Session countdown and sleep overlay state management.
- **Architecture**: Single Activity Compose Navigation architecture with modular, self-contained screen composables under `com.pocketdev.airisgame.ui.screens`.

---

## 🚀 Building & Running

### Prerequisites
- Android SDK 35 (compileSdk: 36, minSdk: 28, targetSdk: 36)
- Java 17+ / OpenJDK 17
- Gradle 8.14+

### Build Debug APK
```bash
./gradlew assembleDebug

# Output APK path:
# app/build/outputs/apk/debug/app-debug.apk
# app/build/outputs/apk/debug/airisworld.apk
```

---

## 💖 Dedicated to Airis
*A lovingly crafted early childhood education app built to make learning playful, intuitive, and joyful!*
