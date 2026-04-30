package jp.co.mixi.monsterstr.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import jp.co.mixi.monsterstr.data.Prefs
import jp.co.mixi.monsterstr.game.Levels
import jp.co.mixi.monsterstr.ui.common.JokerAnimatedBackground
import jp.co.mixi.monsterstr.ui.common.JokerButton
import jp.co.mixi.monsterstr.ui.common.JokerButtonStyle
import jp.co.mixi.monsterstr.ui.common.LocalPrefs
import jp.co.mixi.monsterstr.ui.common.pressableWithCooldown
import jp.co.mixi.monsterstr.ui.theme.JokerCardEdge
import jp.co.mixi.monsterstr.ui.theme.JokerGold
import jp.co.mixi.monsterstr.ui.theme.JokerGreen
import jp.co.mixi.monsterstr.ui.theme.JokerInk
import jp.co.mixi.monsterstr.ui.theme.JokerOnSurface
import jp.co.mixi.monsterstr.ui.theme.JokerPurpleDeep
import jp.co.mixi.monsterstr.ui.theme.JokerPurpleNight
import jp.co.mixi.monsterstr.ui.theme.Hellhot100Theme

@Composable
fun LevelsScreen(
    onLevelClick: (Int) -> Unit,
    onBack: () -> Unit,
) {
    val prefs: Prefs = LocalPrefs.current

    Box(modifier = Modifier.fillMaxSize()) {
        JokerAnimatedBackground(intensity = 0.6f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 24.dp)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "LEVELS",
                style = MaterialTheme.typography.displayMedium.copy(color = JokerGold),
            )
            Text(
                text = "${prefs.maxUnlockedLevel}/${Prefs.TOTAL_LEVELS} unlocked",
                style = MaterialTheme.typography.titleMedium.copy(color = JokerGreen),
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(Levels.all) { config ->
                    val unlocked = prefs.isUnlocked(config.level)
                    val completed = prefs.isLevelCompleted(config.level)
                    val best = prefs.bestScore(config.level)
                    LevelCard(
                        levelNumber = config.level,
                        targetScore = config.targetScore,
                        attempts = config.attempts,
                        unlocked = unlocked,
                        completed = completed,
                        bestScore = best,
                        onClick = {
                            if (unlocked) onLevelClick(config.level)
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            JokerButton(
                text = "BACK",
                style = JokerButtonStyle.Ghost,
                leadingGlyph = "←",
                onClick = onBack,
            )
        }
    }
}

@Composable
private fun LevelCard(
    levelNumber: Int,
    targetScore: Int,
    attempts: Int,
    unlocked: Boolean,
    completed: Boolean,
    bestScore: Int,
    onClick: () -> Unit,
) {
    val baseColors = if (completed) {
        listOf(JokerPurpleDeep, JokerGreen.copy(alpha = 0.45f))
    } else if (unlocked) {
        listOf(JokerPurpleNight, JokerPurpleDeep)
    } else {
        listOf(JokerInk, Color.Black.copy(alpha = 0.7f))
    }
    val edge = if (completed) JokerGreen else if (unlocked) JokerGold else JokerCardEdge
    val contentAlpha = if (unlocked) 1f else 0.55f

    Box(
        modifier = Modifier
            .aspectRatio(0.85f)
            .pressableWithCooldown(enabled = unlocked, onClick = onClick)
            .shadow(
                elevation = if (unlocked) 6.dp else 0.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = edge,
                spotColor = edge,
            )
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(baseColors))
            .border(1.5.dp, edge.copy(alpha = 0.7f), RoundedCornerShape(18.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "$levelNumber",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 30.sp,
                    color = JokerOnSurface.copy(alpha = contentAlpha),
                ),
                textAlign = TextAlign.Center,
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "🎯 $targetScore",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = JokerGold.copy(alpha = contentAlpha),
                        fontSize = 12.sp,
                    ),
                )
                Text(
                    text = "🔄 $attempts turns",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = JokerOnSurface.copy(alpha = contentAlpha * 0.85f),
                        fontSize = 11.sp,
                    ),
                )
                if (bestScore > 0) {
                    Text(
                        text = "★ $bestScore",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = JokerGreen,
                            fontSize = 11.sp,
                        ),
                    )
                }
            }
        }

        if (!unlocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "🔒",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 38.sp),
                )
            }
        }
    }
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
    Hellhot100Theme {
        CompositionLocalProvider(LocalPrefs provides prefs) {
            LevelsScreen(onLevelClick = {}, onBack = {})
        }
    }
}