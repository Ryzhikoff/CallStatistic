package evgeniy.ryzhikov.callstatistics

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.util.Log
import com.yandex.mobile.ads.common.InitializationListener
import com.yandex.mobile.ads.common.MobileAds
import evgeniy.ryzhikov.callstatistics.di.AppComponent
import evgeniy.ryzhikov.callstatistics.di.DaggerAppComponent
import evgeniy.ryzhikov.callstatistics.di.modules.DatabaseModule
import evgeniy.ryzhikov.callstatistics.di.modules.DomainModule

class App: Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        if (isMainProcess()) {
            instance = this

            appComponent = DaggerAppComponent.builder()
                .domainModule(DomainModule(this))
                .databaseModule(DatabaseModule())
                .build()

            initializeYandexAds()
        }
    }

    private fun initializeYandexAds() {
        MobileAds.initialize(this, object : InitializationListener {
            override fun onInitializationCompleted() {
                Log.d(YANDEX_MOBILE_ADS_TAG, "Yandex ADS SDK initialized")
            }
        })
    }

    private fun isMainProcess(): Boolean {
        return packageName == getCurrentProcessName()
    }

    private fun getCurrentProcessName(): String {
        val mypid = Process.myPid()
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = manager.runningAppProcesses
        for (info in infos) {
            if (info.pid == mypid) {
                return info.processName
            }
        }
        // may never return null
        return ""
    }


    companion object {
        lateinit var instance: App
            private set
        const val YANDEX_MOBILE_ADS_TAG = "YandexMobileAds"
    }

}