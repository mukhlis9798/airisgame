package com.pocketdev.airisgame.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Renders a crisp real photo from drawable resources if available,
 * gracefully falling back to a colorful emoji/vector icon if missing.
 */
@Composable
fun RealImageOrEmoji(
    imageRes: Int?,
    emoji: String,
    modifier: Modifier = Modifier,
    emojiSize: TextUnit = 50.sp,
    cornerRadius: Dp = 18.dp,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (imageRes != null && imageRes != 0) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius))
            )
        } else {
            Text(
                text = emoji,
                fontSize = emojiSize
            )
        }
    }
}
