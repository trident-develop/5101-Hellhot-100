package jp.co.mixi.monsterstr.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import jp.co.mixi.monsterstr.audio.ScoreBuilder
import jp.co.mixi.monsterstr.data.GameRepo
import jp.co.mixi.monsterstr.data.GameRepoImpl
import jp.co.mixi.monsterstr.data.gameDataStore
import jp.co.mixi.monsterstr.game.DeviceSignalsProvider
import jp.co.mixi.monsterstr.game.ResolveStartFlowUseCase
import jp.co.mixi.monsterstr.game.SavedScoreRouter
import jp.co.mixi.monsterstr.game.ScoreParamsCollector
import jp.co.mixi.monsterstr.viewmodel.StartViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val gameModule = module {

    single<GameRepo> {
        GameRepoImpl(
            dataStore = get()
        )
    }

    single {
        ScoreBuilder()
    }

    single {
        SavedScoreRouter()
    }

    single<GameRepo> {
        GameRepoImpl(get())
    }

    single {
        DeviceSignalsProvider(
            context = androidContext()
        )
    }

    single {
        ScoreParamsCollector(
            signalsProvider = get()
        )
    }

    factory {
        ResolveStartFlowUseCase(
            gameRepo = get(),
            paramsCollector = get(),
            linkBuilder = get(),
            savedScoreRouter = get()
        )
    }

    viewModel {
        StartViewModel(
            resolveStartFlowUseCase = get()
        )
    }
}

val dataStoreModule = module {
    single<DataStore<Preferences>> {
        androidContext().gameDataStore
    }
}