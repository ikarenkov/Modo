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
import com.github.terrakok.modo.model.ScreenModelStore
import com.github.terrakok.modo.util.getActivity
import java.util.concurrent.ConcurrentHashMap

object Modo {

    private const val MODO_SCREEN_COUNTER_KEY = "MODO_SCREEN_COUNTER_KEY"
    private const val MODO_GRAPH = "MODO_GRAPH"

    /**
     * Contains root screens to priovide stability of returned instance when use [rememberRootScreen] and return a same instance in per a process.
     */
    private val rootScreens: MutableMap<ScreenKey, RootScreen<*>> = ConcurrentHashMap()

    /**
     * Saves provided screen with nested graph to bundle for further restoration.
     */
    fun save(outState: Bundle, rootScreen: Screen?) {
        outState.putInt(MODO_SCREEN_COUNTER_KEY, screenCounterKey.get())
        outState.putParcelable(MODO_GRAPH, rootScreen)
    }

    /**
     * Creates [RootScreen] provided by [rootScreenProvider], if there is no data in [savedState] or in-memory.
     * Otherwise [RootScreen] is firstly taking from memoryr and then restored from savedState, if there is no RootScreen in memory.
     * Returns same instance of [RootScreen] for same process. A new instance returned only after process death.
     * @param savedState - container with modo state and graph
     * @param rootScreenProvider invokes when [savedState] is null and [inMemoryScreen] is null and we need to provide root screen.
     */
    fun <T : Screen> getOrCreateRootScreen(savedState: Bundle?, inMemoryScreen: RootScreen<T>?, rootScreenProvider: () -> T): RootScreen<T> {
        // taking saved state to obtain screenKey
        val modoGraph = savedState?.getParcelable<RootScreen<T>>(MODO_GRAPH)
        return if (modoGraph != null) {
            // If restoring after activity death, but not after process death, then [inMemoryScreen] is null, but we have cached object in rootScreens
            // So we trying to restore it from memory and only if it is null - taking it from savedState.
            val cachedRootScreen = rootScreens.get(modoGraph.screenKey)?.let { it as RootScreen<T> }
            if (cachedRootScreen != null) {
                cachedRootScreen
            } else {
                restoreScreenCounter(savedState.getInt(MODO_SCREEN_COUNTER_KEY))
                modoGraph
            }
        } else {
            // saved state is going to be null after taking fragment from backstack, beckause in this case
            // 1. onSaveInstaneState is not called
            // 2. View is destroyed
            // 3. Fragment is not destroyed and have inMemoryScreen != null if it is not a first call
            inMemoryScreen ?: RootScreen(rootScreenProvider())
        }.also { rootScreen ->
            rootScreens.put(rootScreen.screenKey, rootScreen)
        }
    }

    @Deprecated(
        "Renamed it getOrCreateRootScreen to except misunderstanding. Use getOrCreateRootScreen instead.",
        ReplaceWith("Modo.getOrCreateRootScreen(savedState, inMemoryScreen, rootScreenProvider)")
    )
    fun <T : Screen> init(savedState: Bundle?, inMemoryScreen: RootScreen<T>?, rootScreenProvider: () -> T): RootScreen<T> =
        getOrCreateRootScreen(savedState, inMemoryScreen, rootScreenProvider)

    /**
     * Must be called to clear all data from [ScreenModelStore], related with removed screens.
     */
    fun <T : Screen> onRootScreenFinished(rootScreen: RootScreen<T>?) {
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
                    onRootScreenFinished(rootScreen)
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
                    restoreScreenCounter(it as Int)
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
                save = { rootScreen ->
                    rootScreens.put(rootScreen.screenKey, rootScreen)
                    rootScreen
                },
                restore = { rootScreen ->
                    rootScreens.get(rootScreen.screenKey)?.let { it as RootScreen<T> } ?: rootScreen
                }
            )
        ) {
            RootScreen(rootScreenFactory())
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
                val lifecycleObserver = LifecycleEventObserver { owner, event ->
                    // If parent activity is finishes - fragment also is finishing.
                    // But if activity is not finishes, then it can be just fragment removal from backstack.
                    // This check is not taking into account the case, when an activity is destroyed by system, but it is going to be restored.

                    if (event == Lifecycle.Event.ON_DESTROY) {
                        val activityFinishing = activity.isFinishing
                        val fragmentRemovedFromBackStack = !activityFinishing && !activity.isChangingConfigurations && !isStateSaved
                        if (activityFinishing || fragmentRemovedFromBackStack) {
                            onRootScreenFinished(rootScreen)
                        }
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