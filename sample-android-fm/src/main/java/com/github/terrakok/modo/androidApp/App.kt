package com.github.terrakok.modo.androidApp

import android.app.Application
import com.github.terrakok.modo.LogReducer
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.MultiReducer
import com.github.terrakok.modo.android.AppReducer

class App : Application() {

    override fun onCreate() {
        modo = Modo(LogReducer(AppReducer(this, MultiReducer())))
        super.onCreate()
    }

    companion object {
        lateinit var modo: Modo
            private set
    }
}