package jp.jiho.cmpprofiledemo.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initKoinCommon(additionalConfig: KoinApplication.() -> Unit = {}) {
    startKoin {
        additionalConfig()
        modules(appModules)
    }
}
