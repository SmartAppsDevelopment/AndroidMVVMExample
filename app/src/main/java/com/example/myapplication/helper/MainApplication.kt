package com.example.myapplication.helper

import android.app.Application
import com.example.myapplication.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}