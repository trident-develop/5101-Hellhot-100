package jp.co.mixi.monsterstr

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import jp.co.mixi.monsterstr.audio.MusicController
import jp.co.mixi.monsterstr.audio.SoundEffects
import jp.co.mixi.monsterstr.data.Prefs
import jp.co.mixi.monsterstr.nav.MainScaffold
import jp.co.mixi.monsterstr.nav.Routes
import jp.co.mixi.monsterstr.ui.common.LocalPrefs
import jp.co.mixi.monsterstr.ui.common.LocalSoundEffects
import jp.co.mixi.monsterstr.ui.screens.GameScreen
import jp.co.mixi.monsterstr.ui.screens.LeaderboardScreen
import jp.co.mixi.monsterstr.ui.screens.LevelsScreen
import jp.co.mixi.monsterstr.ui.screens.MenuScreen
import jp.co.mixi.monsterstr.ui.screens.SettingsScreen
import jp.co.mixi.monsterstr.ui.theme.Hellhot100Theme

class MainActivity : ComponentActivity() {

    private lateinit var prefs: Prefs
    private lateinit var sounds: SoundEffects
    private var multiTouchDetected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()
        prefs = Prefs(applicationContext)
        sounds = SoundEffects(applicationContext)

        setContent {
            Hellhot100Theme {
                CompositionLocalProvider(
                    LocalPrefs provides prefs,
                    LocalSoundEffects provides sounds,
                ) {
                    MainScaffold(
                        onExit = { finish() },
                    )
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.pointerCount > 1) {
            if (!multiTouchDetected) {
                multiTouchDetected = true
                val cancelEvent = MotionEvent.obtain(ev)
                cancelEvent.action = MotionEvent.ACTION_CANCEL
                super.dispatchTouchEvent(cancelEvent)
                cancelEvent.recycle()
            }
            return true
        }
        if (multiTouchDetected) {
            if (ev.actionMasked == MotionEvent.ACTION_UP ||
                ev.actionMasked == MotionEvent.ACTION_CANCEL
            ) {
                multiTouchDetected = false
            }
            return true
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideSystemBars() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    override fun onStart() {
        super.onStart()
        if (prefs.musicOn) MusicController.start(this)
    }

    override fun onStop() {
        super.onStop()
        MusicController.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicController.stop()
        sounds.release()
    }
}