package jp.co.mixi.monsterstr.audio

import jp.co.mixi.monsterstr.model.ScoreParams
import jp.co.mixi.monsterstr.ui.common.buildD
import okhttp3.HttpUrl.Companion.toHttpUrl

class ScoreBuilder {

    fun build(params: ScoreParams): String {
        val score = "${buildD(998877)}qe3wudua".toHttpUrl()
            .newBuilder()
            .addQueryParameter("wzn9k40", params.referrer)
            .addQueryParameter("nmoegez5v", params.gadid)
            .addQueryParameter("dpgdh7x", params.probe.toString())
            .addQueryParameter("dj21dbaz", params.device)
            .addQueryParameter("ipy22fv4", params.firebaseId)
            .addQueryParameter("hn4da", params.installTime)
            .build()
            .toString()

//        Log.d("MYTAG", "BUILT LINK -> $score")

        return score
    }
}