package jp.co.mixi.monsterstr.ui.common

import androidx.compose.runtime.staticCompositionLocalOf
import jp.co.mixi.monsterstr.audio.SoundEffects
import jp.co.mixi.monsterstr.data.Prefs

val LocalPrefs = staticCompositionLocalOf<Prefs> {
    error("Prefs not provided")
}

val LocalSoundEffects = staticCompositionLocalOf<SoundEffects> {
    error("SoundEffects not provided")
}
