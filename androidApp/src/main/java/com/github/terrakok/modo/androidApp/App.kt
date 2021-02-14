package com.github.terrakok.modo.androidApp

import android.app.Application
import com.github.terrakok.modo.AppReducer
import com.github.terrakok.modo.LogReducer
import com.github.terrakok.modo.Modo

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