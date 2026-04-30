package jp.co.mixi.monsterstr.game

import androidx.compose.ui.graphics.Color
import jp.co.mixi.monsterstr.ui.theme.JokerGold
import jp.co.mixi.monsterstr.ui.theme.JokerGreen
import jp.co.mixi.monsterstr.ui.theme.JokerPurple
import jp.co.mixi.monsterstr.ui.theme.JokerRed

enum class Symbol(
    val glyph: String,
    val baseValue: Int,
    val weight: Int,
    val isWild: Boolean,
    val accent: Color,
) {
    CHERRY("🍒", 10, 22, false, JokerRed),
    LEMON("🍋", 15, 20, false, JokerGold),
    GRAPE("🍇", 25, 18, false, JokerPurple),
    WATERMELON("🍉", 40, 14, false, JokerGreen),
    JOKER_HAT("🎩", 60, 12, false, JokerPurple),
    JOKER_MASK("🎭", 90, 9, false, JokerRed),
    LAUGHING_CARD("🃏", 130, 7, false, JokerGold),
    PURPLE_DIAMOND("💜", 180, 5, false, JokerPurple),
    GREEN_STAR("🌟", 240, 3, false, JokerGreen),
    WILD_JOKER("🤡", 0, 4, true, JokerGold),
}
