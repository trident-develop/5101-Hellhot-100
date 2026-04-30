package jp.co.mixi.monsterstr.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.co.mixi.monsterstr.ui.theme.JokerCardEdge
import jp.co.mixi.monsterstr.ui.theme.JokerGold
import jp.co.mixi.monsterstr.ui.theme.JokerGreen
import jp.co.mixi.monsterstr.ui.theme.JokerGreenDeep
import jp.co.mixi.monsterstr.ui.theme.JokerInk
import jp.co.mixi.monsterstr.ui.theme.JokerOnSurface
import jp.co.mixi.monsterstr.ui.theme.JokerPurple
import jp.co.mixi.monsterstr.ui.theme.JokerPurpleDeep
import jp.co.mixi.monsterstr.ui.theme.JokerRed
import jp.co.mixi.monsterstr.ui.theme.JokerRedDeep

enum class JokerButtonStyle { Primary, Accent, Danger, Ghost }

@Composable
fun JokerButton(
    text: String,
    modifier: Modifier = Modifier,
    style: JokerButtonStyle = JokerButtonStyle.Primary,
    leadingGlyph: String? = null,
    enabled: Boolean = true,
    cornerRadius: Dp = 22.dp,
    cooldownMillis: Long = 1000L,
    onClick: () -> Unit,
) {
    val gradient: Brush = when (style) {
        JokerButtonStyle.Primary -> Brush.horizontalGradient(
            listOf(JokerPurpleDeep, JokerPurple, JokerPurpleDeep)
        )
        JokerButtonStyle.Accent -> Brush.horizontalGradient(
            listOf(JokerGreenDeep, JokerGreen.copy(alpha = 0.85f), JokerGreenDeep)
        )
        JokerButtonStyle.Danger -> Brush.horizontalGradient(
            listOf(JokerRedDeep, JokerRed, JokerRedDeep)
        )
        JokerButtonStyle.Ghost -> Brush.horizontalGradient(
            listOf(JokerInk, JokerCardEdge.copy(alpha = 0.5f), JokerInk)
        )
    }
    val borderColor: Color = when (style) {
        JokerButtonStyle.Accent -> JokerGold
        JokerButtonStyle.Danger -> JokerGold
        else -> JokerGold.copy(alpha = 0.6f)
    }
    val contentColor: Color = if (style == JokerButtonStyle.Accent) JokerInk else JokerOnSurface

    val baseAlpha = if (enabled) 1f else 0.45f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .pressableWithCooldown(
                cooldownMillis = cooldownMillis,
                enabled = enabled,
                onClick = onClick,
            )
            .shadow(
                elevation = if (enabled) 10.dp else 0.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = JokerPurple,
                spotColor = JokerPurple,
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(gradient)
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(borderColor.copy(alpha = baseAlpha), Color.Transparent, borderColor.copy(alpha = baseAlpha))
                ),
                shape = RoundedCornerShape(cornerRadius),
            )
            .padding(PaddingValues(horizontal = 20.dp, vertical = 14.dp)),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor.copy(alpha = baseAlpha)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                if (leadingGlyph != null) {
                    Text(text = leadingGlyph, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
                )
            }
        }
    }
}
