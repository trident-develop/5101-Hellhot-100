package jp.co.mixi.monsterstr.nav

object Routes {
    const val MENU = "menu"
    const val LEVELS = "levels"
    const val SETTINGS = "settings"
    const val LEADERBOARD = "leaderboard"

    const val GAME_PATTERN = "game/{level}"
    fun game(level: Int) = "game/$level"
    const val GAME_ARG_LEVEL = "level"
    const val LOADING = "loading"
    const val CONNECT = "connect"
}
