package com.github.terrakok.modo.sample

import android.app.Application
import com.github.terrakok.modo.ModoDevOptions
import com.github.terrakok.modo.sample.logs.logcat
import logcat.AndroidLogcatLogger
import logcat.LogPriority

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
        ModoDevOptions.onIllegalLifecycleUpdate = ModoDevOptions.ValidationFailedStrategy { throwable ->
            throw throwable
        }
        ModoDevOptions.onScreenDisposeListener = {
            it.logcat { "Screen disposed" }
        }
        ModoDevOptions.onScreenPreDisposeListener = {
            it.logcat { "Screen preDisposed" }
        }
    }
}