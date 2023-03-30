package com.github.terrakok.androidcomposeapp

import android.app.Application
import logcat.AndroidLogcatLogger
import logcat.LogPriority

class ModoSampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
    }
}