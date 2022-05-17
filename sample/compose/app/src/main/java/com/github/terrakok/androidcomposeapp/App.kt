package com.github.terrakok.androidcomposeapp

import android.app.Application
import com.github.terrakok.modo.LogReducer
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.MultiReducer
import com.github.terrakok.modo.NestedNavigationReducer
import com.github.terrakok.modo.android.compose.AppReducer
import modo.sample.compose.navigation.core.CustomModoReducer

class App : Application() {
    val modo = Modo(LogReducer(AppReducer(this, MultiReducer(NestedNavigationReducer(CustomModoReducer())))))

    override fun onCreate() {
        INSTANCE = this
        super.onCreate()
    }

    companion object {
        lateinit var INSTANCE: App
            private set
    }
}