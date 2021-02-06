package com.github.terrakok.modo.androidApp

import android.app.Application
import com.github.terrakok.modo.LogReducer
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.ModoReducer

class App : Application() {
    val modo = Modo(LogReducer(ModoReducer()))

    override fun onCreate() {
        INSTANCE = this
        super.onCreate()
    }

    companion object {
        lateinit var INSTANCE: App
            private set
    }
}