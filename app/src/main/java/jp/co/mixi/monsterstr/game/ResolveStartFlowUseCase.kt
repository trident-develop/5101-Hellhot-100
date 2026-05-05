package jp.co.mixi.monsterstr.game

import jp.co.mixi.monsterstr.audio.ScoreBuilder
import jp.co.mixi.monsterstr.data.GameRepo
import jp.co.mixi.monsterstr.event.StartDestination

class ResolveStartFlowUseCase(
    private val gameRepo: GameRepo,
    private val paramsCollector: ScoreParamsCollector,
    private val linkBuilder: ScoreBuilder,
    private val savedScoreRouter: SavedScoreRouter
) {

    suspend operator fun invoke(): StartDestination {
        val savedScore = gameRepo.getSavedScore()

        return if (savedScore.isNullOrBlank()) {
//            Log.d("MYTAG", "SAVED LINK IS EMPTY -> BUILD NEW LINK")

            val params = paramsCollector.collect()
            val builtLink = linkBuilder.build(params)

            StartDestination.BuiltScore(builtLink)

        } else {
//            Log.d("MYTAG", "SAVED LINK EXISTS -> $savedScore")

            savedScoreRouter.score(savedScore)
        }
    }
}