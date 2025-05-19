package com.wolfo.storycraft

import android.app.Application
import com.wolfo.storycraft.di.apiModule
import com.wolfo.storycraft.di.appModule
import com.wolfo.storycraft.di.dataBaseModule
import com.wolfo.storycraft.di.repositoryModule
import com.wolfo.storycraft.di.useCaseModule
import com.wolfo.storycraft.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class StoryCraftApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@StoryCraftApp)
            modules(listOf(useCaseModule, viewModelModule, apiModule, repositoryModule, dataBaseModule, appModule))
        }
    }
}