package com.github.terrakok.androidcomposeapp

import android.app.Application
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.android.compose.AppReducer
import com.github.terrakok.modo.android.compose.LogReducer

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