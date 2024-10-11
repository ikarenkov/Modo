package io.github.ikarenkov.workshop

import android.app.Application
import com.github.terrakok.modo.ModoDevOptions
import io.github.ikarenkov.workshop.di.rootModule
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.logcat
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class WorkshopApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
        ModoDevOptions.onIllegalScreenModelStoreAccess = ModoDevOptions.ValidationFailedStrategy { throwable ->
            throw throwable
        }
        ModoDevOptions.onIllegalClearState = ModoDevOptions.ValidationFailedStrategy { throwable ->
            logcat(priority = LogPriority.ERROR) { "Cleaning state of composable, which still can be visible for user." }
        }

        startKoin {
            androidLogger()
            androidContext(this@WorkshopApp)
            modules(rootModule)
        }
    }
}