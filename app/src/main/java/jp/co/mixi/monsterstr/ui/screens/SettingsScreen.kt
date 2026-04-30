package jp.co.mixi.monsterstr.ui.screens

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import jp.co.mixi.monsterstr.audio.MusicController
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

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val prefs: Prefs = LocalPrefs.current
    val context = LocalContext.current
    val isInPreview      = LocalInspectionMode.current
    var musicOn by remember { mutableStateOf(prefs.musicOn) }
    var roundSoundOn by remember { mutableStateOf(prefs.roundSoundOn) }
    var spinSoundOn by remember { mutableStateOf(prefs.spinSoundOn) }

    var howToPlayOpen by remember { mutableStateOf(false) }
    var privacyOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        JokerAnimatedBackground(intensity = 0.5f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 24.dp)
                .padding(top = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.displayMedium.copy(color = JokerGold),
            )
            Spacer(modifier = Modifier.height(20.dp))

            ToggleRow(
                title = "Background music",
                subtitle = "Plays only inside the main app",
                checked = musicOn,
                onCheckedChange = { value ->
                    musicOn = value
                    prefs.musicOn = value
                    MusicController.setEnabled(context, value)
                },
            )
            Spacer(modifier = Modifier.height(12.dp))
            ToggleRow(
                title = "Win / Lose sound",
                subtitle = "Round results play a sound",
                checked = roundSoundOn,
                onCheckedChange = { value ->
                    roundSoundOn = value
                    prefs.roundSoundOn = value
                },
            )
            Spacer(modifier = Modifier.height(12.dp))
            ToggleRow(
                title = "Turning sound",
                subtitle = "Reels rumble while they turn",
                checked = spinSoundOn,
                onCheckedChange = { value ->
                    spinSoundOn = value
                    prefs.spinSoundOn = value
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            JokerButton(
                text = "HOW TO PLAY",
                style = JokerButtonStyle.Primary,
                leadingGlyph = "❓",
                onClick = { howToPlayOpen = true },
            )
            Spacer(modifier = Modifier.height(12.dp))
            JokerButton(
                text = "PRIVACY POLICY",
                style = JokerButtonStyle.Primary,
                leadingGlyph = "🔐",
                onClick = { privacyOpen = true },
            )

            Spacer(modifier = Modifier.height(20.dp))

            JokerButton(
                text = "BACK",
                style = JokerButtonStyle.Ghost,
                leadingGlyph = "←",
                onClick = onBack,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (!isInPreview) {
            AndroidView(
                factory = {
                    val adView = AdView(it)
                    adView.setAdSize(AdSize.BANNER)
                    adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
                    adView.loadAd(AdRequest.Builder().build())
                    adView
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    if (howToPlayOpen) {
        InfoDialog(
            title = "HOW TO PLAY",
            body = """
                • Tap TURN to roll the reels.
                • Match five or more Joker symbols and fruits.
                • Reach the target score before your turns run out.
                • Wild Joker (🤡) substitutes any symbol and adds bonus points.
                • Each level you complete unlocks the next one.
                • Bigger clusters pay much more — chase the green star! 🌟
            """.trimIndent(),
            onClose = { howToPlayOpen = false },
        )
    }

    if (privacyOpen) {
        PrivacyDialog (
            title = "PRIVACY POLICY",
            onClose = { privacyOpen = false },
        )
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = JokerPurple,
                spotColor = JokerPurple,
            )
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(listOf(JokerPurpleDeep, JokerPurpleNight))
            )
            .border(1.5.dp, JokerCardEdge, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = JokerOnSurface,
                    fontSize = 16.sp,
                ),
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = JokerGreen.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                ),
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = JokerInk,
                checkedTrackColor = JokerGreen,
                checkedBorderColor = JokerGold,
                uncheckedThumbColor = JokerOnSurface.copy(alpha = 0.7f),
                uncheckedTrackColor = JokerInk,
                uncheckedBorderColor = JokerCardEdge,
            ),
        )
    }
}

@Composable
private fun InfoDialog(
    title: String,
    body: String,
    onClose: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(28.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 22.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = JokerPurple,
                    spotColor = JokerPurple,
                )
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(listOf(JokerPurpleDeep, JokerInk))
                )
                .border(2.dp, JokerGold, RoundedCornerShape(24.dp))
                .padding(22.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(JokerInk)
                    .border(1.dp, JokerGreen, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "🃏", style = MaterialTheme.typography.displayMedium)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(color = JokerGold),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = JokerOnSurface,
                    fontSize = 14.sp,
                ),
            )
            Spacer(modifier = Modifier.height(18.dp))
            JokerButton(
                text = "CLOSE",
                style = JokerButtonStyle.Accent,
                onClick = onClose,
            )
        }
    }
}

@Composable
private fun PrivacyDialog(
    title: String,
    onClose: () -> Unit,
) {
    var loadWeb by remember { mutableStateOf(true) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(28.dp)
                .fillMaxSize()
                .shadow(
                    elevation = 22.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = JokerPurple,
                    spotColor = JokerPurple,
                )
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(listOf(JokerPurpleDeep, JokerInk))
                )
                .border(2.dp, JokerGold, RoundedCornerShape(24.dp))
                .padding(22.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(JokerInk)
                    .border(1.dp, JokerGreen, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "🃏", style = MaterialTheme.typography.displayMedium)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(color = JokerGold),
            )
            Spacer(modifier = Modifier.height(12.dp))
            AndroidView(
                factory = { context ->
                    FrameLayout(context).apply {
                        val webView = WebView(context).apply {
                            setInitialScale(100)
                            settings.setSupportZoom(true)
                            settings.builtInZoomControls = true
                            settings.displayZoomControls = false
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    loadWeb = false
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    return false
                                }
                            }
                            loadUrl("https://telegra.ph/Privacy-Policy-for-Hellhot-100-04-30")
                        }
                        addView(
                            webView, FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(18.dp))
            JokerButton(
                text = "CLOSE",
                style = JokerButtonStyle.Accent,
                onClick = onClose,
            )
        }

        if (loadWeb) {
            LinearProgressIndicator(
                color = Color.Yellow,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .align(Alignment.Center)
            )
        }
    }
}