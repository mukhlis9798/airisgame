package com.pocketdev.airisgame.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pocketdev.airisgame.audio.SoundEngine
import com.pocketdev.airisgame.core.LanguageManager
import com.pocketdev.airisgame.ui.theme.CandyCoral
import com.pocketdev.airisgame.ui.theme.CoralShadow
import com.pocketdev.airisgame.ui.theme.SkyVibrant
import com.pocketdev.airisgame.ui.theme.TextDark
import kotlin.random.Random

@Composable
fun ParentMathGateDialog(
    title: String = if (LanguageManager.isMalay()) "Zon Ibu Bapa Sahaja" else "Parents Zone Only",
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var num1 by remember { mutableStateOf(Random.nextInt(4, 9)) }
    var num2 by remember { mutableStateOf(Random.nextInt(2, 6)) }
    val correctAnswer = remember(num1, num2) { num1 + num2 }

    val options = remember(correctAnswer) {
        val wrongOptions = mutableSetOf<Int>()
        while (wrongOptions.size < 3) {
            val offset = Random.nextInt(-4, 5)
            val candidate = correctAnswer + offset
            if (candidate != correctAnswer && candidate > 0) {
                wrongOptions.add(candidate)
            }
        }
        (wrongOptions.toList() + correctAnswer).shuffled()
    }

    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White)
                .padding(22.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = CandyCoral,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (LanguageManager.isMalay())
                        "Sila selesaikan soalan matematik berikut untuk membuktikan anda adalah orang dewasa:"
                    else
                        "Please solve the math question below to confirm you are a parent/grown-up:",
                    fontSize = 13.5.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Math Equation Display
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "$num1 + $num2 = ?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark,
                        letterSpacing = 2.sp
                    )
                }

                if (isError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (LanguageManager.isMalay()) "Jawapan salah, sila cuba lagi!" else "Incorrect, please try again!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CandyCoral
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Options Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    options.forEach { option ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clickable {
                                    if (option == correctAnswer) {
                                        SoundEngine.playCelebrationFanfare()
                                        onSuccess()
                                    } else {
                                        SoundEngine.playWobble()
                                        isError = true
                                        // Refresh problem
                                        num1 = Random.nextInt(4, 9)
                                        num2 = Random.nextInt(2, 6)
                                    }
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SkyVibrant),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$option",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                ToddlerButton(
                    text = if (LanguageManager.isMalay()) "Batal" else "Cancel",
                    buttonColor = Color(0xFF94A3B8),
                    shadowColor = Color(0xFF64748B),
                    height = 44.dp,
                    onClick = onDismiss
                )
            }
        }
    }
}
