package com.github.terrakok.androidcomposeapp

import android.app.Application
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.ModoReducer

class App : Application() {
    val modo = Modo(ModoReducer())

    override fun onCreate() {
        INSTANCE = this
        super.onCreate()
    }

    companion object {
        lateinit var INSTANCE: App
            private set
    }
}