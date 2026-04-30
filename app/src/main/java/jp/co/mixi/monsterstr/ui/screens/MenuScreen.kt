package jp.co.mixi.monsterstr.ui.screens

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.co.mixi.monsterstr.ui.common.JokerAnimatedBackground
import jp.co.mixi.monsterstr.ui.common.JokerButton
import jp.co.mixi.monsterstr.ui.common.JokerButtonStyle
import jp.co.mixi.monsterstr.ui.common.JokerMask
import jp.co.mixi.monsterstr.ui.common.JokerMood
import jp.co.mixi.monsterstr.ui.theme.JokerGold
import jp.co.mixi.monsterstr.ui.theme.JokerGreen
import jp.co.mixi.monsterstr.ui.theme.JokerRed

@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onLevels: () -> Unit,
    onLeaderboard: () -> Unit,
    onSettings: () -> Unit,
    onExit: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "MenuTitle")
    val titleScale by transition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "titleScale",
    )
    val titleHueShift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "titleHue",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        JokerAnimatedBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            JokerMask(size = 110.dp, mood = JokerMood.Idle)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "HELLHOT 100",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 42.sp,
                    color = lerpJokerHue(titleHueShift),
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = titleScale
                        scaleY = titleScale
                    }
                    .shadow(elevation = 0.dp),
            )

            Spacer(modifier = Modifier.height(40.dp))

            val buttonModifier = Modifier
                .widthIn(max = 320.dp)

            JokerButton(
                text = "PLAY",
                style = JokerButtonStyle.Accent,
                leadingGlyph = "🃏",
                modifier = buttonModifier,
                onClick = onPlay,
            )
            Spacer(modifier = Modifier.height(14.dp))
            JokerButton(
                text = "LEADERBOARD",
                style = JokerButtonStyle.Primary,
                leadingGlyph = "🏆",
                modifier = buttonModifier,
                onClick = onLeaderboard,
            )
            Spacer(modifier = Modifier.height(14.dp))
            JokerButton(
                text = "SETTINGS",
                style = JokerButtonStyle.Primary,
                leadingGlyph = "⚙️",
                modifier = buttonModifier,
                onClick = onSettings,
            )
            Spacer(modifier = Modifier.height(14.dp))
            JokerButton(
                text = "EXIT",
                style = JokerButtonStyle.Danger,
                leadingGlyph = "🚪",
                modifier = buttonModifier,
                onClick = onExit,
            )
        }
    }
}

private fun lerpJokerHue(t: Float): Color {
    val a = JokerGold
    val b = JokerRed
    val c = JokerGreen
    return when {
        t < 0.5f -> lerpColor(a, b, t * 2f)
        else -> lerpColor(b, c, (t - 0.5f) * 2f)
    }
}

private fun lerpColor(a: Color, b: Color, t: Float): Color {
    val tt = t.coerceIn(0f, 1f)
    return Color(
        red = a.red + (b.red - a.red) * tt,
        green = a.green + (b.green - a.green) * tt,
        blue = a.blue + (b.blue - a.blue) * tt,
        alpha = a.alpha + (b.alpha - a.alpha) * tt,
    )
}
