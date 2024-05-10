package com.github.terrakok.modo.sample

import android.app.Application
import com.github.terrakok.modo.ModoDevOptions
import logcat.AndroidLogcatLogger
import logcat.LogPriority

class ModoSampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
        ModoDevOptions.onIllegalScreenModelStoreAccess = ModoDevOptions.ValidationFailedStrategy { throwable ->
            throw throwable
        }
    }
}