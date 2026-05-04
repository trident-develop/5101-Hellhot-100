package jp.co.mixi.monsterstr.audio

import android.content.Context
import android.media.MediaPlayer
import ua.prom.R

object MusicController {

    private var player: MediaPlayer? = null

    @Synchronized
    fun start(context: Context) {
        if (player != null) return
        player = MediaPlayer.create(context.applicationContext, R.raw.game_music)?.apply {
            isLooping = true
            setVolume(0.45f, 0.45f)
            try {
                start()
            } catch (_: IllegalStateException) {
                release()
            }
        }
    }

    @Synchronized
    fun stop() {
        val current = player ?: return
        try {
            if (current.isPlaying) current.stop()
        } catch (_: IllegalStateException) {
            // ignore
        }
        current.release()
        player = null
    }

    @Synchronized
    fun setEnabled(context: Context, enabled: Boolean) {
        if (enabled) start(context) else stop()
    }
}
