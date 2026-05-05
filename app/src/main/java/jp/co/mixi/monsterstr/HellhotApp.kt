package jp.co.mixi.monsterstr

import android.app.Application
import jp.co.mixi.monsterstr.di.dataStoreModule
import jp.co.mixi.monsterstr.di.gameModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class HellhotApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@HellhotApp)
            modules(
                dataStoreModule,
                gameModule
            )
        }
    }
}