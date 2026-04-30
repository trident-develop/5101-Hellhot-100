package jp.co.mixi.monsterstr.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import jp.co.mixi.monsterstr.audio.SoundEffects
import jp.co.mixi.monsterstr.data.Prefs
import jp.co.mixi.monsterstr.game.Levels
import jp.co.mixi.monsterstr.game.REEL_COLUMNS
import jp.co.mixi.monsterstr.game.REEL_ROWS
import jp.co.mixi.monsterstr.game.SlotEngine
import jp.co.mixi.monsterstr.game.SpinResult
import jp.co.mixi.monsterstr.game.Symbol
import jp.co.mixi.monsterstr.ui.common.JokerAnimatedBackground
import jp.co.mixi.monsterstr.ui.common.JokerButton
import jp.co.mixi.monsterstr.ui.common.JokerButtonStyle
import jp.co.mixi.monsterstr.ui.common.JokerMask
import jp.co.mixi.monsterstr.ui.common.JokerMood
import jp.co.mixi.monsterstr.ui.common.LocalPrefs
import jp.co.mixi.monsterstr.ui.common.LocalSoundEffects
import jp.co.mixi.monsterstr.ui.theme.JokerCardEdge
import jp.co.mixi.monsterstr.ui.theme.JokerGold
import jp.co.mixi.monsterstr.ui.theme.JokerGreen
import jp.co.mixi.monsterstr.ui.theme.JokerInk
import jp.co.mixi.monsterstr.ui.theme.JokerOnSurface
import jp.co.mixi.monsterstr.ui.theme.JokerPurple
import jp.co.mixi.monsterstr.ui.theme.JokerPurpleDeep
import jp.co.mixi.monsterstr.ui.theme.JokerPurpleNight
import jp.co.mixi.monsterstr.ui.theme.JokerRed
import jp.co.mixi.monsterstr.ui.theme.Hellhot100Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private enum class GameOutcome { Won, Lost }

@Composable
fun GameScreen(
    level: Int,
    onBackToLevels: () -> Unit,
) {
    val prefs: Prefs = LocalPrefs.current
    val sounds: SoundEffects = LocalSoundEffects.current
    val config = remember(level) { Levels.get(level) }

    var score by remember(level) { mutableIntStateOf(0) }
    var attemptsLeft by remember(level) { mutableIntStateOf(config.attempts) }
    var grid by remember(level) {
        mutableStateOf(
            List(REEL_ROWS) {
                List(REEL_COLUMNS) { SlotEngine.randomWeightedSymbol() }
            }
        )
    }
    var winningCells by remember(level) { mutableStateOf(emptySet<Pair<Int, Int>>()) }
    var spinning by remember(level) { mutableStateOf(false) }
    var stoppedCols by remember(level) { mutableIntStateOf(REEL_COLUMNS) }
    var lastSpinScore by remember(level) { mutableIntStateOf(0) }
    var mood by remember(level) { mutableStateOf(JokerMood.Idle) }
    var outcome by remember(level) { mutableStateOf<GameOutcome?>(null) }

    val scope = rememberCoroutineScope()

    fun spin() {
        if (spinning || outcome != null || attemptsLeft <= 0) return
        spinning = true
        winningCells = emptySet()
        stoppedCols = 0
        if (prefs.spinSoundOn) sounds.playSpin()

        scope.launch {
            val rng = Random.Default
            val result: SpinResult = SlotEngine.spin(rng)

            val initialFullSpinMs = 480L
            val perColumnDelayMs = 180L
            val frameIntervalMs = 55L

            val startTime = System.currentTimeMillis()
            var stopped = 0
            while (stopped < REEL_COLUMNS) {
                val elapsed = System.currentTimeMillis() - startTime
                val newStopped = if (elapsed < initialFullSpinMs) {
                    0
                } else {
                    (((elapsed - initialFullSpinMs) / perColumnDelayMs) + 1)
                        .toInt()
                        .coerceAtMost(REEL_COLUMNS)
                }
                if (newStopped != stopped) stoppedCols = newStopped
                stopped = newStopped

                grid = List(REEL_ROWS) { r ->
                    List(REEL_COLUMNS) { c ->
                        if (c < stopped) result.grid[r][c]
                        else randomSymbol(rng)
                    }
                }
                if (stopped >= REEL_COLUMNS) break
                delay(frameIntervalMs)
            }

            grid = result.grid
            winningCells = result.winningCells
            lastSpinScore = result.totalScore
            score += result.totalScore
            attemptsLeft -= 1
            sounds.stopSpin()

            spinning = false

            if (score >= config.targetScore) {
                outcome = GameOutcome.Won
                mood = JokerMood.Happy
                prefs.markLevelCompleted(level)
                prefs.submitScore(level, score)
                if (prefs.roundSoundOn) sounds.playWin()
            } else if (attemptsLeft <= 0) {
                outcome = GameOutcome.Lost
                mood = JokerMood.Sad
                prefs.submitScore(level, score)
                if (prefs.roundSoundOn) sounds.playLose()
            } else {
                mood = if (result.totalScore > 0) JokerMood.Happy else JokerMood.Idle
            }
        }
    }

    val progress = (score.toFloat() / config.targetScore).coerceIn(0f, 1f)

    Box(modifier = Modifier.fillMaxSize()) {
        JokerAnimatedBackground(intensity = 0.4f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp)
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                StatusBlock(
                    title = "LEVEL",
                    value = "$level",
                    accent = JokerPurple,
                )
                JokerMask(size = 64.dp, mood = mood)
                StatusBlock(
                    title = "TARGET",
                    value = "${config.targetScore}",
                    accent = JokerGold,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            ScoreBar(score = score, target = config.targetScore, progress = progress)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                StatusPill(label = "Turns", value = "$attemptsLeft / ${config.attempts}", color = JokerGreen)
                StatusPill(
                    label = "Last win",
                    value = if (lastSpinScore == 0) "—" else "+$lastSpinScore",
                    color = if (lastSpinScore > 0) JokerGold else JokerCardEdge,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SlotGrid(
                grid = grid,
                winningCells = winningCells,
                stoppedCols = stoppedCols,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )

            Spacer(modifier = Modifier.height(14.dp))

            JokerButton(
                text = if (spinning) "TURNING…" else "TURN",
                style = JokerButtonStyle.Accent,
                leadingGlyph = "🎰",
                enabled = !spinning && outcome == null && attemptsLeft > 0,
                cooldownMillis = 250L,
                onClick = { spin() },
            )

            Spacer(modifier = Modifier.height(10.dp))

            JokerButton(
                text = "BACK TO LEVELS",
                style = JokerButtonStyle.Ghost,
                leadingGlyph = "←",
                onClick = onBackToLevels,
            )
        }

        AnimatedVisibility(
            visible = outcome != null,
            enter = fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.85f),
            exit = fadeOut(animationSpec = tween(160)) + scaleOut(targetScale = 0.9f),
        ) {
            outcome?.let { o ->
                ResultDialog(
                    outcome = o,
                    score = score,
                    target = config.targetScore,
                    onRetry = {
                        score = 0
                        attemptsLeft = config.attempts
                        winningCells = emptySet()
                        grid = List(REEL_ROWS) {
                            List(REEL_COLUMNS) { SlotEngine.randomWeightedSymbol() }
                        }
                        stoppedCols = REEL_COLUMNS
                        lastSpinScore = 0
                        mood = JokerMood.Idle
                        outcome = null
                    },
                    onBack = onBackToLevels,
                )
            }
        }
    }
}

@Composable
private fun StatusBlock(title: String, value: String, accent: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = accent,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
            ),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                color = JokerOnSurface,
                fontSize = 22.sp,
            ),
        )
    }
}

@Composable
private fun StatusPill(label: String, value: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(JokerPurpleNight.copy(alpha = 0.7f))
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium.copy(color = color, fontSize = 12.sp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(color = JokerOnSurface, fontSize = 14.sp),
        )
    }
}

@Composable
private fun ScoreBar(score: Int, target: Int, progress: Float) {
    val animated by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600, easing = EaseOutCubic),
        label = "ScoreBar",
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "SCORE",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = JokerGreen,
                    letterSpacing = 2.sp,
                ),
            )
            Text(
                text = "$score / $target",
                style = MaterialTheme.typography.titleMedium.copy(color = JokerOnSurface),
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(JokerInk.copy(alpha = 0.7f))
                .border(1.dp, JokerCardEdge, RoundedCornerShape(8.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { scaleX = animated; transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f) }
                    .background(
                        Brush.horizontalGradient(
                            listOf(JokerGreen, JokerGold, JokerRed)
                        )
                    ),
            )
        }
    }
}

@Composable
private fun SlotGrid(
    grid: List<List<Symbol>>,
    winningCells: Set<Pair<Int, Int>>,
    stoppedCols: Int,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.widthIn(max = 560.dp),
        contentAlignment = Alignment.Center,
    ) {
        val targetRatio = REEL_COLUMNS.toFloat() / REEL_ROWS.toFloat()
        val widthFromHeight = maxHeight * targetRatio
        val finalWidth = if (widthFromHeight <= maxWidth) widthFromHeight else maxWidth
        val finalHeight = finalWidth / targetRatio

        val cellSpacing = (finalWidth.value * 0.020f).coerceIn(4f, 10f).dp
        val outerPadding = (finalWidth.value * 0.028f).coerceIn(6f, 14f).dp
        val outerCorner = (finalWidth.value * 0.055f).coerceIn(14f, 26f).dp

        Box(
            modifier = Modifier
                .size(width = finalWidth, height = finalHeight)
                .shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(outerCorner),
                    ambientColor = JokerPurple,
                    spotColor = JokerPurple,
                )
                .clip(RoundedCornerShape(outerCorner))
                .background(Brush.verticalGradient(listOf(JokerPurpleNight, JokerInk)))
                .border(2.dp, JokerGold.copy(alpha = 0.7f), RoundedCornerShape(outerCorner))
                .padding(outerPadding),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(cellSpacing),
            ) {
                for (r in 0 until REEL_ROWS) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(cellSpacing),
                    ) {
                        for (c in 0 until REEL_COLUMNS) {
                            SlotCell(
                                symbol = grid[r][c],
                                winning = (r to c) in winningCells,
                                spinning = c >= stoppedCols,
                                stopOrder = c,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotCell(
    symbol: Symbol,
    winning: Boolean,
    spinning: Boolean,
    stopOrder: Int,
    modifier: Modifier = Modifier,
) {
    val infinite = rememberInfiniteTransition(label = "SlotCell")
    val winPulse by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(520),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "winPulse",
    )
    val winGlow by infinite.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(620),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "winGlow",
    )
    val spinJitter by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(160 + stopOrder * 25, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "spinJitter",
    )
    val landBounce by animateFloatAsState(
        targetValue = if (spinning) 0.94f else 1f,
        animationSpec = if (spinning) {
            tween(durationMillis = 120, easing = EaseOutCubic)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium,
            )
        },
        label = "land",
    )

    val borderColor = when {
        winning -> JokerGold
        symbol.isWild -> JokerGreen
        else -> JokerCardEdge
    }
    val bg = if (winning) {
        Brush.verticalGradient(
            listOf(
                symbol.accent.copy(alpha = 0.55f * winGlow),
                JokerPurpleDeep,
            )
        )
    } else {
        Brush.verticalGradient(listOf(JokerPurpleDeep, JokerInk))
    }

    BoxWithConstraints(modifier = modifier) {
        val sizeDp = if (maxWidth < maxHeight) maxWidth else maxHeight
        val sizeValue = sizeDp.value
        val fontSize = (sizeValue * 0.55f).coerceIn(18f, 44f).sp
        val cornerRadius = (sizeValue * 0.18f).coerceIn(8f, 16f).dp
        val cellHeightPx = with(androidx.compose.ui.platform.LocalDensity.current) {
            maxHeight.toPx()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val s = if (winning) winPulse * landBounce else landBounce
                    scaleX = s
                    scaleY = s
                    translationY = if (spinning) {
                        sin(spinJitter * 2f * PI.toFloat()) * cellHeightPx * 0.15f
                    } else {
                        0f
                    }
                    alpha = if (spinning) 0.78f else 1f
                }
                .shadow(
                    elevation = if (winning) 8.dp else 2.dp,
                    shape = RoundedCornerShape(cornerRadius),
                    ambientColor = if (winning) JokerGold else JokerPurple,
                    spotColor = if (winning) JokerGold else JokerPurple,
                )
                .clip(RoundedCornerShape(cornerRadius))
                .background(bg)
                .border(1.5.dp, borderColor, RoundedCornerShape(cornerRadius)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = symbol.glyph,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = fontSize),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ResultDialog(
    outcome: GameOutcome,
    score: Int,
    target: Int,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    val isWin = outcome == GameOutcome.Won
    val title = if (isWin) "JACKPOT!" else "NO LUCK"
    val subtitle = if (isWin) "The Joker approves." else "The Joker laughs at you."
    val accent = if (isWin) JokerGreen else JokerRed
    val mood = if (isWin) JokerMood.Happy else JokerMood.Sad

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { /* consume taps */ } }
            .background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 22.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = accent,
                    spotColor = accent,
                )
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(JokerPurpleDeep, JokerInk, JokerPurpleNight)
                    )
                )
                .border(2.dp, accent, RoundedCornerShape(28.dp))
                .padding(horizontal = 22.dp, vertical = 24.dp),
        ) {
            JokerMask(size = 100.dp, mood = mood)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium.copy(
                    color = accent,
                    fontSize = 36.sp,
                ),
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = JokerOnSurface.copy(alpha = 0.85f),
                ),
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Final score: $score",
                style = MaterialTheme.typography.titleLarge.copy(color = JokerGold),
            )
            Text(
                text = "Target: $target",
                style = MaterialTheme.typography.titleMedium.copy(color = JokerOnSurface.copy(alpha = 0.75f)),
            )
            Spacer(modifier = Modifier.height(18.dp))
            JokerButton(
                text = if (isWin) "BACK TO LEVELS" else "RETRY",
                style = if (isWin) JokerButtonStyle.Accent else JokerButtonStyle.Danger,
                leadingGlyph = if (isWin) "✓" else "↻",
                onClick = if (isWin) onBack else onRetry,
            )
            Spacer(modifier = Modifier.height(10.dp))
            JokerButton(
                text = if (isWin) "PLAY AGAIN" else "BACK TO LEVELS",
                style = JokerButtonStyle.Ghost,
                onClick = if (isWin) onRetry else onBack,
            )
        }
    }
}

private fun randomSymbol(rng: Random): Symbol {
    val all = Symbol.values()
    return all[rng.nextInt(all.size)]
}

@Preview(
    showBackground = true,
    showSystemUi = true
)

@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 360,
    heightDp = 640
)

@Preview(
    name = "mdpi (160)",
    widthDp = 320,
    heightDp = 680,
    fontScale = 1.0f,
    showBackground = true,
    showSystemUi = true
)

@Preview(
    name = "hdpi (240)",
    widthDp = 450,
    heightDp = 800,
    fontScale = 1.0f,
    showBackground = true,
    showSystemUi = true
)

@Composable
private fun ScreenPreview() {
    val context = LocalContext.current
    val prefs = remember { Prefs(context) }
    val sounds = remember { SoundEffects(context) }
    Hellhot100Theme {
        CompositionLocalProvider(
            LocalPrefs provides prefs,
            LocalSoundEffects provides sounds,
        ) {
            GameScreen(level = 17, onBackToLevels = {})
        }
    }
}