package jp.co.mixi.monsterstr.data

import android.content.Context
import androidx.core.content.edit

class Prefs(context: Context) {

    private val sp = context.applicationContext
        .getSharedPreferences("hellhot_prefs", Context.MODE_PRIVATE)

    var maxUnlockedLevel: Int
        get() = sp.getInt(KEY_MAX_UNLOCKED, 1).coerceIn(1, TOTAL_LEVELS)
        set(value) = sp.edit { putInt(KEY_MAX_UNLOCKED, value.coerceIn(1, TOTAL_LEVELS)) }

    fun isUnlocked(level: Int): Boolean = level <= maxUnlockedLevel

    fun isLevelCompleted(level: Int): Boolean =
        sp.getBoolean(keyCompleted(level), false)

    fun markLevelCompleted(level: Int) {
        sp.edit { putBoolean(keyCompleted(level), true) }
        if (level + 1 <= TOTAL_LEVELS && level + 1 > maxUnlockedLevel) {
            maxUnlockedLevel = level + 1
        }
    }

    fun bestScore(level: Int): Int = sp.getInt(keyBest(level), 0)

    fun submitScore(level: Int, score: Int) {
        if (score > bestScore(level)) {
            sp.edit { putInt(keyBest(level), score) }
        }
    }

    fun completedCount(): Int =
        (1..TOTAL_LEVELS).count { isLevelCompleted(it) }

    fun bestTotalScore(): Int =
        (1..TOTAL_LEVELS).sumOf { bestScore(it) }

    fun topScores(limit: Int = 10): List<Pair<Int, Int>> =
        (1..TOTAL_LEVELS)
            .map { it to bestScore(it) }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(limit)

    var musicOn: Boolean
        get() = sp.getBoolean(KEY_MUSIC_ON, true)
        set(value) = sp.edit { putBoolean(KEY_MUSIC_ON, value) }

    var roundSoundOn: Boolean
        get() = sp.getBoolean(KEY_ROUND_SOUND_ON, true)
        set(value) = sp.edit { putBoolean(KEY_ROUND_SOUND_ON, value) }

    var spinSoundOn: Boolean
        get() = sp.getBoolean(KEY_SPIN_SOUND_ON, true)
        set(value) = sp.edit { putBoolean(KEY_SPIN_SOUND_ON, value) }

    companion object {
        const val TOTAL_LEVELS = 36

        private const val KEY_MAX_UNLOCKED = "max_unlocked_level"
        private const val KEY_MUSIC_ON = "music_on"
        private const val KEY_ROUND_SOUND_ON = "round_sound_on"
        private const val KEY_SPIN_SOUND_ON = "spin_sound_on"
        private fun keyCompleted(level: Int) = "completed_$level"
        private fun keyBest(level: Int) = "best_$level"
    }
}
