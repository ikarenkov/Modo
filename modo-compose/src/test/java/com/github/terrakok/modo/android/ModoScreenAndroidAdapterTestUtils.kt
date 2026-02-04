package com.github.terrakok.modo.android

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.github.terrakok.modo.model.ScreenModelStore

object ModoScreenAndroidAdapterTestUtils {

    fun setupArchTaskExecutor() {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) = runnable.run()
            override fun postToMainThread(runnable: Runnable) = runnable.run()
            override fun isMainThread() = true
        })
    }

    fun cleanupArchTaskExecutor() {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    fun cleanupScreenModelStore() {
        ScreenModelStore.removedScreenKeys.clear()
        ScreenModelStore.screenModels.clear()
        ScreenModelStore.dependencies.clear()
    }
}

class TestLifecycleOwner : LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
}
