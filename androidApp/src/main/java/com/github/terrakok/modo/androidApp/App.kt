package com.github.terrakok.modo.androidApp

import android.app.Application
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.android.AppReducer
import com.github.terrakok.modo.android.LogReducer

class App : Application() {
    val modo = Modo(LogReducer(AppReducer(this)))

    override fun onCreate() {
        INSTANCE = this
        super.onCreate()
    }

    companion object {
        lateinit var INSTANCE: App
            private set
    }
}