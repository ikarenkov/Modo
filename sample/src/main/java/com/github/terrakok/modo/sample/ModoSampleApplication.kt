package com.github.terrakok.modo.sample

import android.app.Application
import com.github.terrakok.modo.ModoDevOptions
import com.github.terrakok.modo.util.log
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.logcat

class ModoSampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
        ModoDevOptions.onIllegalScreenModelStoreAccess = ModoDevOptions.ValidationFailedStrategy { throwable ->
            throw throwable
        }
        ModoDevOptions.onIllegalClearState = ModoDevOptions.ValidationFailedStrategy { throwable ->
            throw throwable
        }
        ModoDevOptions.onScreenDisposeListener = {
            it.log("Screen disposed")
        }
        ModoDevOptions.onScreenPreDisposeListener = {
            it.log("Screen preDisposed")
        }
    }
}