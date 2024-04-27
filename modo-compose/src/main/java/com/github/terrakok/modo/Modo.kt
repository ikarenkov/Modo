package com.github.terrakok.modo

import android.app.Activity
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.terrakok.modo.model.ScreenModelStore

object Modo {

    private const val MODO_SCREEN_COUNTER_KEY = "MODO_SCREEN_COUNTER_KEY"
    private const val MODO_GRAPH = "MODO_GRAPH"

    /**
     * Saves provided screen with nested graph to bundle for further restoration.
     */
    fun save(outState: Bundle, rootScreen: Screen?) {
        outState.putInt(MODO_SCREEN_COUNTER_KEY, screenCounterKey.get())
        outState.putParcelable(MODO_GRAPH, rootScreen)
    }

    /**
     * Entrance point for screen integration.
     * @param savedState - container with modo state and graph
     * @param rootScreenProvider invokes when [savedState] is null and [inMemoryScreen] is null and we need to provide root screen.
     */
    fun <T : Screen> init(savedState: Bundle?, inMemoryScreen: RootScreen<T>?, rootScreenProvider: () -> T): RootScreen<T> {
        val modoGraph = savedState?.getParcelable<RootScreen<T>>(MODO_GRAPH)
        return if (modoGraph != null) {
            restoreScreenCounter(savedState.getInt(MODO_SCREEN_COUNTER_KEY))
            modoGraph
        } else {
            inMemoryScreen ?: RootScreen(rootScreenProvider())
        }
    }

    /**
     * Must be called to clear all data from [ScreenModelStore], related with removed screens.
     */
    fun <T : Screen> onRootScreenFinished(rootScreen: RootScreen<T>?) {
        rootScreen?.let(::clearScreenModel)
    }

    /**
     * Creates [RootScreen] with provided screen, if there is no saved value. Otherwise [RootScreen] is restored from savedState.
     * It automatically clears all data from [ScreenModelStore], .
     * It also saves and restores screenCounterKey for correct [generateScreenKey] usage.
     * Integration point for your screen hierarchy. You can use this fun to integrate Modo to your Fragment or Activity.
     */
    @Composable
    fun <T : Screen> Activity.rememberRootScreen(
        rootScreenFactory: () -> T
    ): RootScreen<T> {
        rememberSaveable<Int>(
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
        val rootScreen = rememberSaveable(key = MODO_GRAPH) {
            RootScreen(rootScreenFactory())
        }
        DisposableEffect(rootScreen, this) {
            onDispose {
                if (isFinishing) {
                    onRootScreenFinished(rootScreen)
                }
            }
        }
        return rootScreen
    }

    private fun clearScreenModel(screen: Screen) {
        ScreenModelStore.remove(screen)
        (screen as? ContainerScreen<*, *>)?.navigationState?.getChildScreens()?.forEach(::clearScreenModel)
    }

}