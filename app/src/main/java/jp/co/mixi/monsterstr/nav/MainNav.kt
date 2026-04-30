package jp.co.mixi.monsterstr.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import jp.co.mixi.monsterstr.ui.screens.GameScreen
import jp.co.mixi.monsterstr.ui.screens.LeaderboardScreen
import jp.co.mixi.monsterstr.ui.screens.LevelsScreen
import jp.co.mixi.monsterstr.ui.screens.MenuScreen
import jp.co.mixi.monsterstr.ui.screens.SettingsScreen

@Composable
fun MainScaffold(onExit: () -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.MENU) {
        composable(Routes.MENU) {
            MenuScreen(
                onPlay = { navController.navigate(Routes.LEVELS) },
                onLevels = { navController.navigate(Routes.LEVELS) },
                onLeaderboard = { navController.navigate(Routes.LEADERBOARD) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onExit = onExit,
            )
        }
        composable(Routes.LEVELS) {
            LevelsScreen(
                onLevelClick = { level -> navController.navigate(Routes.game(level)) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.GAME_PATTERN,
            arguments = listOf(navArgument(Routes.GAME_ARG_LEVEL) { type = NavType.IntType }),
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getInt(Routes.GAME_ARG_LEVEL) ?: 1
            GameScreen(
                level = level,
                onBackToLevels = {
                    navController.popBackStack(Routes.LEVELS, inclusive = false)
                },
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(onBack = { navController.popBackStack() })
        }
    }
}
