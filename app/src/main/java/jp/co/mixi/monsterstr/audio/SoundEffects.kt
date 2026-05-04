package jp.co.mixi.monsterstr.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import ua.prom.R

class SoundEffects(context: Context) {

    private val pool: SoundPool? = runCatching {
        SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }.getOrNull()

    private val winId: Int = loadSafe(context, R.raw.level_win)
    private val loseId: Int = loadSafe(context, R.raw.level_lose)
    private val spinId: Int = loadSafe(context, R.raw.slot_rounded)

    private var spinStream: Int = 0

    private fun loadSafe(context: Context, resId: Int): Int =
        pool?.let { runCatching { it.load(context, resId, 1) }.getOrDefault(0) } ?: 0

    fun playWin() {
        val p = pool ?: return
        if (winId != 0) p.play(winId, 1f, 1f, 1, 0, 1f)
    }

    fun playLose() {
        val p = pool ?: return
        if (loseId != 0) p.play(loseId, 1f, 1f, 1, 0, 1f)
    }

    fun playSpin() {
        val p = pool ?: return
        stopSpin()
        if (spinId != 0) {
            spinStream = p.play(spinId, 0.9f, 0.9f, 1, 0, 1f)
        }
    }

    fun stopSpin() {
        val p = pool ?: return
        if (spinStream != 0) {
            p.stop(spinStream)
            spinStream = 0
        }
    }

    fun release() {
        pool?.release()
    }
}
