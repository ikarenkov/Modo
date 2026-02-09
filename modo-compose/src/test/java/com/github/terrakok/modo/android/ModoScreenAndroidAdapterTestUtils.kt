package com.github.terrakok.modo.android

import android.os.Bundle
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

    var lifecycleState: Lifecycle.State
        set(value) {
            lifecycleRegistry.currentState = value
        }
        get() = lifecycleRegistry.currentState

}

/**
 * Test helper that emulates the behavior of LifecycleDisposableEffect composable.
 * Allows manual control over composition lifecycle and parent state for testing.
 */
class CompositionLifecycleEmulator(
    private val adapter: ModoScreenAndroidAdapter,
    private val parentLifecycleOwner: TestLifecycleOwner,
) {

    private var manualResumePause: Boolean = false
    private val savedState = Bundle()
    private var unsubscribeFromParent: (() -> Unit)? = null
    private var isInComposition = false

    init {
        initializeAdapter()
        adapter.atomicParentLifecycleOwner.set(parentLifecycleOwner)
    }

    fun enterComposition(
        manualResumePause: Boolean = false,
        isActivityFinishing: () -> Boolean? = { false },
        isChangingConfigurations: () -> Boolean? = { false }
    ) {
        check(!isInComposition) { "Already in composition" }
        isInComposition = true

        this.manualResumePause = manualResumePause

        adapter.handleLifecycleOnCompositionEnter(manualResumePause)

        unsubscribeFromParent = adapter.subscribeToParentLifecycle(
            parentLifecycleOwner = parentLifecycleOwner,
            savedState = savedState,
            isActivityFinishing = isActivityFinishing,
            isChangingConfigurations = isChangingConfigurations
        )
    }

    fun exitComposition() {
        check(isInComposition) { "Not in composition" }
        isInComposition = false

        unsubscribeFromParent?.invoke()
        adapter.handleLifecycleOnCompositionExit(manualResumePause)
    }

    fun showTransitionFinished() {
        adapter.showTransitionFinished()
    }

    fun hideTransitionStarted() {
        adapter.hideTransitionStarted()
    }

    val lifecycleState: Lifecycle.State
        get() = adapter.lifecycle.currentState

    val parentState: Lifecycle.State
        get() = parentLifecycleOwner.lifecycle.currentState

    private fun initializeAdapter() {
        val onCreate = ModoScreenAndroidAdapter::class.java
            .getDeclaredMethod("onCreate", Bundle::class.java)
        onCreate.isAccessible = true
        onCreate.invoke(adapter, savedState)
    }
}
