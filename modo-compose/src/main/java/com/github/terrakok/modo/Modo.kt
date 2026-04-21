package com.github.terrakok.modo

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.github.terrakok.modo.Modo.rememberRootScreen
import com.github.terrakok.modo.Modo.rootScreens
import com.github.terrakok.modo.Modo.save
import com.github.terrakok.modo.model.ScreenModelStore
import com.github.terrakok.modo.util.getActivity
import java.util.concurrent.ConcurrentHashMap

object Modo {

    private const val MODO_SCREEN_COUNTER_KEY = "MODO_SCREEN_COUNTER_KEY"
    private const val MODO_GRAPH = "MODO_GRAPH"

    /**
     * Contains root screens to priovide stability of returned instance when use [rememberRootScreen] and return a same instance in per a process.
     */
    @org.jetbrains.annotations.VisibleForTesting
    internal val rootScreens: MutableMap<ScreenKey, RootScreen<*>> = ConcurrentHashMap()

    /**
     * Saves provided screen with nested graph to bundle for further restoration.
     */
    @Deprecated("Use rememberRootScreen, which handles saving and restoring automatically. Will be removed in 1.0.")
    fun save(outState: Bundle, rootScreen: Screen?) {
        outState.putInt(MODO_SCREEN_COUNTER_KEY, screenCounterKey.get())
        outState.putParcelable(MODO_GRAPH, rootScreen)
    }

    /**
     * Returns the [RootScreen] for this host (Activity/Fragment), creating it if necessary.
     * Guarantees the same instance is returned within a single process — a new instance is only
     * created after process death.
     *
     * There are three scenarios, resolved in priority order:
     *
     * **1. Bundle restore** (`savedState != null`):
     * The host was recreated by the system (config change or process death).
     * - Config change (same process): [rootScreens] already holds the live instance → returned from cache.
     * - Process death: [rootScreens] is empty → the graph is deserialized from [savedState],
     *   stored in [rootScreens], and returned. [screenCounterKey] is restored to avoid key collisions.
     *
     * **2. In-memory hit** (`savedState == null`, [inMemoryScreen] != null):
     * The host's view was destroyed without saving state (e.g. a Fragment going to the back stack).
     * The Fragment is still alive and holds the previous [RootScreen] reference.
     * [inMemoryScreen] provides the key to retrieve the same instance from [rootScreens].
     *
     * **3. First initialization** (`savedState == null`, [inMemoryScreen] == null):
     * Fresh start — [rootScreenProvider] is called to build the initial screen, a new [RootScreen]
     * is created, stored in [rootScreens], and returned.
     *
     * @param savedState bundle produced by [save], or null on first launch / back-stack return.
     * @param inMemoryScreen existing [RootScreen] held by a non-destroyed Fragment (scenario 2).
     *   Must be null for Activities and for the very first Fragment creation.
     * @param rootScreenProvider called only in scenario 3 to construct the initial root screen.
     */
    @Deprecated("Use rememberRootScreen, which handles all lifecycle concerns automatically. Will be removed in 1.0.")
    fun <T : Screen> getOrCreateRootScreen(savedState: Bundle?, inMemoryScreen: RootScreen<T>?, rootScreenProvider: () -> T): RootScreen<T> {
        val savedModoGraph = savedState?.getParcelable<RootScreen<T>>(MODO_GRAPH)
        return if (savedModoGraph != null) {
            // Scenario 1: bundle restore.
            // Config change → cache hit, process death → cache miss, savedModoGraph is stored.
            restoreScreenCounterIfNeeded(savedState.getInt(MODO_SCREEN_COUNTER_KEY))
            @Suppress("UNCHECKED_CAST")
            rootScreens.getOrPut(savedModoGraph.screenKey) { savedModoGraph } as RootScreen<T>
        } else {
            // Scenarios 2 & 3: no saved state.
            // savedState is null after back-stack return because onSaveInstanceState is not called
            // when a Fragment is only stopped (not destroyed), so inMemoryScreen carries the reference.
            val screen = inMemoryScreen ?: RootScreen(rootScreenProvider()) // scenario 3: first init
            @Suppress("UNCHECKED_CAST")
            rootScreens.getOrPut(screen.screenKey) { screen } as RootScreen<T>
        }
    }

    /**
     * Must be called to clear all data from [ScreenModelStore], related with removed screens.
     */
    @Deprecated("Use rememberRootScreen, which handles cleanup automatically. Will be removed in 1.0.")
    fun <T : Screen> onRootScreenFinished(rootScreen: RootScreen<T>?) = finishRootScreen(rootScreen)

    private fun <T : Screen> finishRootScreen(rootScreen: RootScreen<T>?) {
        if (rootScreen != null) {
            Log.d("Modo", "rootScreen removing $rootScreen")
            rootScreens.remove(rootScreen.screenKey)
            clearScreenModel(rootScreen)
        }
    }

    /**
     * Creates [RootScreen] with provided screen, if there is no saved value. Otherwise [RootScreen] is restored from savedState.
     * Returns same instance of [RootScreen] for same process. A new instance returned only after process death.
     * So you can safelly inject returned value to your DI.
     * It automatically clears all data from [ScreenModelStore], .
     * It also saves and restores screenCounterKey for correct [generateScreenKey] usage.
     * Integration point for your screen hierarchy. You can use this fun to integrate Modo to your Fragment or Activity.
     */
    @Composable
    fun <T : Screen> Activity.rememberRootScreen(
        rootScreenFactory: () -> T
    ): RootScreen<T> {
        val rootScreen = rememberCounterAndRoot(rootScreenFactory)
        DisposableEffect(rootScreen, this) {
            onDispose {
                if (isFinishing) {
                    finishRootScreen(rootScreen)
                }
            }
        }
        return rootScreen
    }

    @Composable
    private fun <T : Screen> rememberCounterAndRoot(rootScreenFactory: () -> T): RootScreen<T> {
        rememberSaveable(
            key = MODO_SCREEN_COUNTER_KEY,
            saver = Saver(
                restore = {
                    restoreScreenCounterIfNeeded(it as Int)
                    it
                },
                save = {
                    val counter = screenCounterKey.get()
                    if (counter == -1) {
                        null
                    } else {
                        counter
                    }
                }
            )
        ) {
            screenCounterKey.get()
        }
        val rootScreen = rememberSaveable(
            key = MODO_GRAPH,
            saver = Saver(
                save = { it },
                restore = { saved ->
                    @Suppress("UNCHECKED_CAST")
                    rootScreens.getOrPut(saved.screenKey) { saved } as RootScreen<T>
                }
            )
        ) {
            RootScreen(rootScreenFactory()).also { newRoot ->
                rootScreens[newRoot.screenKey] = newRoot
            }
        }
        return rootScreen
    }

    /**
     * Creates [RootScreen] with provided screen, if there is no saved value. Otherwise [RootScreen] is restored from savedState.
     * Returns same instance of [RootScreen] for same process. A new instance returned only after process death.
     * So you can safelly inject returned value to your DI.
     * It automatically clears all data from [ScreenModelStore], .
     * It also saves and restores screenCounterKey for correct [generateScreenKey] usage.
     * Integration point for your screen hierarchy. You can use this fun to integrate Modo to your Fragment or Activity.
     */
    @Composable
    fun <T : Screen> Fragment.rememberRootScreen(
        rootScreenFactory: () -> T
    ): RootScreen<T> {
        val rootScreen = rememberCounterAndRoot(rootScreenFactory)

        val context = LocalContext.current
        var hasAnyObserver by rememberSaveable {
            mutableStateOf(false)
        }
        DisposableEffect(this) {
            // Add observer only once. We don't care about observer removal because it automatically removes itself ON_DESTROY.
            if (!hasAnyObserver) {
                hasAnyObserver = true
                val activity = context.getActivity()!!
                val lifecycleObserver = LifecycleEventObserver { _, event ->
                    if (isFragmentClosing(event, activity)) {
                        finishRootScreen(rootScreen)
                    }
                }
                lifecycle.addObserver(lifecycleObserver)
            }
            onDispose {
                // we don't remove lifecycleObserver onDispose because onDispose in Composable happens earlier then onDispose in Fragment
            }
        }
        return rootScreen
    }

    private fun clearScreenModel(screen: Screen) {
        ScreenModelStore.remove(screen)
        (screen as? ContainerScreen<*, *>)?.navigationState?.getChildScreens()?.forEach(::clearScreenModel)
    }

}

/**
 * Returns true when the fragment is being permanently destroyed and its state should be cleaned up.
 * Returns false when the fragment will be restored (config change or system-initiated process death).
 */
fun Fragment.isFragmentClosing(event: Lifecycle.Event, activity: Activity): Boolean =
// ON_DESTROY is the only moment when isStateSaved and isChangingConfigurations are reliable.
// Activity finishing covers user-initiated close (back press, finish()).
// The second condition covers fragment removal from backstack:
    // no config change and state wasn't saved means the fragment won't be restored.
    event == Lifecycle.Event.ON_DESTROY &&
        (activity.isFinishing || (!activity.isChangingConfigurations && !isStateSaved))