package jp.co.mixi.monsterstr.ui.common

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.co.mixi.monsterstr.ui.theme.JokerGold
import jp.co.mixi.monsterstr.ui.theme.JokerGreen
import jp.co.mixi.monsterstr.ui.theme.JokerRed

enum class JokerMood { Idle, Happy, Sad }

@Composable
fun JokerMask(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    mood: JokerMood = JokerMood.Idle,
) {
    val transition = rememberInfiniteTransition(label = "JokerMask")
    val pulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    val sway by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "sway",
    )
    val glow by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow",
    )

    val moodScale by animateFloatAsState(
        targetValue = when (mood) {
            JokerMood.Happy -> 1.18f
            JokerMood.Sad -> 0.92f
            JokerMood.Idle -> 1f
        },
        animationSpec = tween(360),
        label = "moodScale",
    )

    val glyph = when (mood) {
        JokerMood.Happy -> "🤣"
        JokerMood.Sad -> "🤪"
        JokerMood.Idle -> "🎭"
    }

    val accent = when (mood) {
        JokerMood.Happy -> JokerGold
        JokerMood.Sad -> JokerRed
        JokerMood.Idle -> JokerGreen
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(accent.copy(alpha = 0.55f * glow), Color.Transparent),
                    center = Offset(this.size.width / 2f, this.size.height / 2f),
                    radius = this.size.minDimension * 0.85f,
                ),
                radius = this.size.minDimension * 0.85f,
                center = Offset(this.size.width / 2f, this.size.height / 2f),
            )
        }
        Text(
            text = glyph,
            modifier = Modifier.graphicsLayer {
                scaleX = pulse * moodScale
                scaleY = pulse * moodScale
                rotationZ = sway
            },
            style = TextStyle(fontSize = (size.value * 0.62f).sp),
            color = Color.White,
        )
    }
}
