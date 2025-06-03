package com.wolfo.storycraft

import android.app.Activity
import android.app.Application
import androidx.activity.compose.LocalActivity
import com.wolfo.storycraft.di.apiModule
import com.wolfo.storycraft.di.appModule
import com.wolfo.storycraft.di.dataBaseModule
import com.wolfo.storycraft.di.repositoryModule
import com.wolfo.storycraft.di.useCaseModule
import com.wolfo.storycraft.di.viewModelModule
import com.wolfo.storycraft.presentation.features.story_list.NativeAdManager
import com.yandex.mobile.ads.common.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.compose.getKoin
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import kotlin.getValue

class StoryCraftApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if(isMainProcess()) {
            startKoin {
                androidLogger(Level.DEBUG)
                androidContext(this@StoryCraftApp)
                modules(listOf(useCaseModule, viewModelModule, apiModule, repositoryModule, dataBaseModule, appModule))
            }


            MobileAds.initialize(this) {

            }

            MobileAds.enableDebugErrorIndicator(true)


            val nativeAdManager: NativeAdManager by inject()
            nativeAdManager.loadAds(4)

        }
    }

    private fun isMainProcess(): Boolean {
        return getProcessName() == "$packageName:main"
    }
}