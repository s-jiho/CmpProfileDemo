package jp.jiho.cmpprofiledemo

import android.app.Application
import jp.jiho.cmpprofiledemo.di.initKoinCommon
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class CmpProfileDemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoinCommon {
            androidContext(this@CmpProfileDemoApplication)
            androidLogger()
        }
    }
}
