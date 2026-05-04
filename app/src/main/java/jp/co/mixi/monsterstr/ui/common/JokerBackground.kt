package jp.co.mixi.monsterstr.ui.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.co.mixi.monsterstr.ui.theme.JokerGreen
import jp.co.mixi.monsterstr.ui.theme.JokerInk
import jp.co.mixi.monsterstr.ui.theme.JokerPurpleDeep
import jp.co.mixi.monsterstr.ui.theme.JokerPurpleNight
import jp.co.mixi.monsterstr.ui.theme.JokerRedDeep
import ua.prom.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class FloatingItem(
    val glyph: String,
    val seedX: Float,
    val seedY: Float,
    val sizeSp: Float,
    val rotationOffset: Float,
    val speed: Float,
    val driftX: Float,
)

@Composable
fun JokerAnimatedBackground(
    modifier: Modifier = Modifier,
    floaters: Boolean = true,
    intensity: Float = 1f,
) {
    val transition = rememberInfiniteTransition(label = "JokerBg")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "phase",
    )
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "drift",
    )

    val glyphs = remember { listOf("🃏", "🎭", "🤡", "💜", "🎩", "🌟") }
    val items = remember(intensity) {
        val rng = Random(0xC0FFEE)
        val count = (16 * intensity).toInt().coerceAtLeast(6)
        List(count) {
            FloatingItem(
                glyph = glyphs[rng.nextInt(glyphs.size)],
                seedX = rng.nextFloat(),
                seedY = rng.nextFloat(),
                sizeSp = 18f + rng.nextFloat() * 28f,
                rotationOffset = rng.nextFloat() * 360f,
                speed = 0.4f + rng.nextFloat() * 1.2f,
                driftX = (rng.nextFloat() - 0.5f) * 0.18f,
            )
        }
    }

    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        JokerInk,
                        lerp(JokerPurpleNight, JokerPurpleDeep, phase),
                        lerp(JokerRedDeep, JokerInk, phase),
                    )
                )
            )
    ) {
        Image(
            painter = painterResource(R.drawable.bg_1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Radial green/purple smoke patches
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(JokerGreen.copy(alpha = 0.18f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(w * (0.18f + 0.08f * phase), h * 0.22f),
                    radius = w * 0.55f,
                ),
                radius = w * 0.55f,
                center = androidx.compose.ui.geometry.Offset(w * (0.18f + 0.08f * phase), h * 0.22f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(JokerPurpleDeep.copy(alpha = 0.55f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(w * (0.85f - 0.08f * phase), h * 0.78f),
                    radius = w * 0.7f,
                ),
                radius = w * 0.7f,
                center = androidx.compose.ui.geometry.Offset(w * (0.85f - 0.08f * phase), h * 0.78f),
            )
        }

        if (floaters) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                items.forEach { item ->
                    val tBase = (drift * item.speed + item.seedY) % 1f
                    val y = (1f - tBase) * (h + 200f) - 100f
                    val sway = sin((drift * 2 * PI * item.speed + item.seedX * PI * 2).toFloat()) * 24f
                    val x = item.seedX * w + sway + item.driftX * w * (drift - 0.5f) * 2f
                    val rotation = (drift * 360f * item.speed + item.rotationOffset) % 360f
                    val sizePx = with(density) { item.sizeSp.sp.toPx() }
                    rotate(degrees = rotation, pivot = androidx.compose.ui.geometry.Offset(x, y)) {
                        drawText(
                            textMeasurer = measurer,
                            text = AnnotatedString(item.glyph),
                            topLeft = androidx.compose.ui.geometry.Offset(x - sizePx / 2f, y - sizePx / 2f),
                            style = TextStyle(
                                fontSize = item.sizeSp.sp,
                                color = Color.White.copy(alpha = 0.18f),
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Suppress("unused")
private fun unitCircle(t: Float, radius: Float): Pair<Float, Float> =
    cos(t * 2 * PI).toFloat() * radius to sin(t * 2 * PI).toFloat() * radius
