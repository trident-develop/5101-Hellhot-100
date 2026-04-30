package jp.co.mixi.monsterstr.game

data class LevelConfig(
    val level: Int,
    val targetScore: Int,
    val attempts: Int,
)

object Levels {

    val all: List<LevelConfig> = (1..36).map { level ->
        val target = when {
            level <= 5 -> 200 + level * 80           // 280..600
            level <= 12 -> 600 + (level - 5) * 110   // 710..1370
            level <= 24 -> 1400 + (level - 12) * 150 // 1550..3200
            else -> 3300 + (level - 24) * 180        // 3480..5460
        }
        // +1 turn every 2 levels, capped at 16
        val attempts = (8 + (level - 1) / 2).coerceAtMost(16)
        LevelConfig(level = level, targetScore = target, attempts = attempts)
    }

    fun get(level: Int): LevelConfig =
        all.firstOrNull { it.level == level } ?: all.first()
}
