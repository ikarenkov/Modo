package com.github.terrakok.modo.sample

import android.app.Application
import androidx.datastore.preferences.preferencesDataStore
import com.github.terrakok.modo.ModoDevOptions
import com.github.terrakok.modo.sample.logs.logcat
import kotlinx.coroutines.MainScope
import logcat.AndroidLogcatLogger
import logcat.LogPriority

private val Application.dataStore by preferencesDataStore(name = "sample_settings")

class ModoSampleApplication : Application() {

    private val applicationScope = MainScope()

    override fun onCreate() {
        super.onCreate()
        SampleAppSettings.init(dataStore, applicationScope)
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
