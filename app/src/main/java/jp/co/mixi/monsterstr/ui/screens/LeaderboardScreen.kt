package jp.co.mixi.monsterstr.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.co.mixi.monsterstr.data.Prefs
import jp.co.mixi.monsterstr.ui.common.JokerAnimatedBackground
import jp.co.mixi.monsterstr.ui.common.JokerButton
import jp.co.mixi.monsterstr.ui.common.JokerButtonStyle
import jp.co.mixi.monsterstr.ui.common.LocalPrefs
import jp.co.mixi.monsterstr.ui.theme.JokerCardEdge
import jp.co.mixi.monsterstr.ui.theme.JokerGold
import jp.co.mixi.monsterstr.ui.theme.JokerGreen
import jp.co.mixi.monsterstr.ui.theme.JokerInk
import jp.co.mixi.monsterstr.ui.theme.JokerOnSurface
import jp.co.mixi.monsterstr.ui.theme.JokerPurple
import jp.co.mixi.monsterstr.ui.theme.JokerPurpleDeep
import jp.co.mixi.monsterstr.ui.theme.JokerPurpleNight
import jp.co.mixi.monsterstr.ui.theme.JokerRed

@Composable
fun LeaderboardScreen(onBack: () -> Unit) {
    val prefs: Prefs = LocalPrefs.current

    val totalBest = remember { prefs.bestTotalScore() }
    val highestUnlocked = remember { prefs.maxUnlockedLevel }
    val completed = remember { prefs.completedCount() }
    val top = remember { prefs.topScores(limit = 10) }

    Box(modifier = Modifier.fillMaxSize()) {
        JokerAnimatedBackground(intensity = 0.55f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 24.dp)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "LEADERBOARD",
                style = MaterialTheme.typography.displayMedium.copy(color = JokerGold),
            )
            Text(
                text = "Local hall of laughter",
                style = MaterialTheme.typography.titleMedium.copy(color = JokerGreen),
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatCard(
                    title = "BEST TOTAL",
                    value = "$totalBest",
                    accent = JokerGold,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    title = "MAX LEVEL",
                    value = "$highestUnlocked",
                    accent = JokerGreen,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    title = "WINS",
                    value = "$completed",
                    accent = JokerRed,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "TOP SCORES",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = JokerOnSurface,
                    letterSpacing = 3.sp,
                ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (top.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(listOf(JokerPurpleDeep, JokerInk))
                        )
                        .border(1.5.dp, JokerCardEdge, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No scores yet.\nTurn a level to start the chaos.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = JokerOnSurface.copy(alpha = 0.7f),
                        ),
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(top) { entry ->
                        ScoreRow(
                            rank = top.indexOf(entry) + 1,
                            level = entry.first,
                            score = entry.second,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

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
private fun StatCard(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = accent,
                spotColor = accent,
            )
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(listOf(JokerPurpleDeep, JokerPurpleNight))
            )
            .border(1.5.dp, accent.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = accent,
                fontSize = 11.sp,
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
private fun ScoreRow(rank: Int, level: Int, score: Int) {
    val accent = when (rank) {
        1 -> JokerGold
        2 -> JokerGreen
        3 -> JokerPurple
        else -> JokerCardEdge
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = accent,
                spotColor = accent,
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(listOf(JokerPurpleDeep, JokerInk))
            )
            .border(1.dp, accent, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.titleLarge.copy(color = accent),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Level $level",
                style = MaterialTheme.typography.titleMedium.copy(color = JokerOnSurface),
            )
        }
        Text(
            text = "$score",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = JokerGold,
                fontSize = 22.sp,
            ),
        )
    }
}

