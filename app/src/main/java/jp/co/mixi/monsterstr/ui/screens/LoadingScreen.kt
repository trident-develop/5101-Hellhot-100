package jp.co.mixi.monsterstr.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.co.mixi.monsterstr.ui.common.JokerAnimatedBackground
import jp.co.mixi.monsterstr.ui.common.JokerMask
import jp.co.mixi.monsterstr.ui.common.JokerMood
import jp.co.mixi.monsterstr.ui.theme.JokerGold
import jp.co.mixi.monsterstr.ui.theme.JokerGreen
import jp.co.mixi.monsterstr.ui.theme.JokerInk
import jp.co.mixi.monsterstr.ui.theme.JokerOnSurface
import jp.co.mixi.monsterstr.ui.theme.JokerPurple
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LoadingContent(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }
    BackHandler(enabled = true) {}
    val transition = rememberInfiniteTransition(label = "Loading")
    val orbit by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "orbit",
    )

    Box(modifier = Modifier.fillMaxSize()) {

        JokerAnimatedBackground(intensity = 1.2f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center,
            ) {
                OrbitingCards(progress = orbit)
                JokerMask(size = 130.dp, mood = JokerMood.Idle)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "HELLHOT 100",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = JokerGold,
                    fontSize = 44.sp,
                ),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "joker is dealing the deck…",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = JokerGreen.copy(alpha = 0.85f),
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(54.dp),
                color = JokerGold,
                trackColor = JokerPurple,
                strokeWidth = 4.dp,
            )
        }
    }
}

@Composable
private fun OrbitingCards(progress: Float) {
    val glyphs = listOf("🃏", "🎭", "🤡", "🎩", "💜", "🌟")
    val measurer = rememberTextMeasurer()
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { rotationZ = progress * 360f },
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.42f
        glyphs.forEachIndexed { index, glyph ->
            val angle = (index.toFloat() / glyphs.size) * 2f * PI.toFloat()
            val x = center.x + cos(angle) * radius
            val y = center.y + sin(angle) * radius
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(JokerPurple.copy(alpha = 0.6f), Color.Transparent),
                    center = Offset(x, y),
                    radius = 60f,
                ),
                radius = 60f,
                center = Offset(x, y),
            )
            drawText(
                textMeasurer = measurer,
                text = AnnotatedString(glyph),
                topLeft = Offset(x - 22f, y - 28f),
                style = TextStyle(
                    fontSize = 28.sp,
                    color = JokerOnSurface,
                ),
            )
        }
        drawCircle(
            color = JokerInk.copy(alpha = 0.25f),
            radius = radius,
            center = center,
            style = Stroke(width = 2f),
        )
    }
}