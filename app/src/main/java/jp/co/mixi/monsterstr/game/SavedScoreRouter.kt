package jp.co.mixi.monsterstr.game

import jp.co.mixi.monsterstr.event.StartDestination
import jp.co.mixi.monsterstr.ui.common.buildD

class SavedScoreRouter {

    fun score(savedScore: String): StartDestination {
        return when {
            !savedScore.startsWith(buildD(998877)) -> {
                StartDestination.OpenSavedScoreTypeA(savedScore)
            }

            savedScore.startsWith(buildD(998877)) -> {
                StartDestination.OpenSavedScoreTypeB(savedScore)
            }

            else -> {
                StartDestination.OpenGame
            }
        }
    }
}