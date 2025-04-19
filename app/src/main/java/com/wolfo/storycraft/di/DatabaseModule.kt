package com.wolfo.storycraft.di

import com.wolfo.storycraft.data.local.db.AppDb
import org.koin.dsl.module

val dataBaseModule = module {

    single<AppDb> {
        AppDb.createDataBase(get())
    }
}