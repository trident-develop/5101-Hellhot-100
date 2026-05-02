package jp.co.mixi.monsterstr.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import jp.co.mixi.monsterstr.ui.theme.JokerOnSurface
import jp.co.mixi.monsterstr.ui.theme.JokerPurple
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
                        .weight(1f),
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
                ) {
                    items(top) { entry ->
                        ScoreRow(
                            rank = top.indexOf(entry) + 1,
                            level = entry.first,
                            score = entry.second,
                        )
                        HorizontalDivider(
                            color = JokerCardEdge.copy(alpha = 0.35f),
                            thickness = 0.6.dp,
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
        modifier = modifier.padding(horizontal = 4.dp, vertical = 6.dp),
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
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                color = JokerOnSurface,
                fontSize = 22.sp,
            ),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(2.dp)
                .background(accent.copy(alpha = 0.7f)),
        )
    }
}

@Composable
private fun ScoreRow(rank: Int, level: Int, score: Int) {
    val accent = when (rank) {
        1 -> JokerGold
        2 -> JokerGreen
        3 -> JokerPurple
        else -> JokerOnSurface.copy(alpha = 0.6f)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 12.dp),
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

