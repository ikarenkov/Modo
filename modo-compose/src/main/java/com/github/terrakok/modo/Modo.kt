package com.github.terrakok.modo

import android.os.Bundle
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
    fun <T : Screen> init(savedState: Bundle?, inMemoryScreen: T?, rootScreenProvider: () -> T): T {
        val modoGraph = savedState?.getParcelable<T>(MODO_GRAPH)
        return if (modoGraph != null) {
            restoreScreenCounter(savedState.getInt(MODO_SCREEN_COUNTER_KEY))
            modoGraph
        } else {
            inMemoryScreen ?: rootScreenProvider()
        }
    }

    /**
     * Must be called to clear all data from [ScreenModelStore], related with removed screens.
     */
    fun onRootScreenFinished(rootScreen: ContainerScreen<*>?) {
        rootScreen?.let(::clearScreenModel)
    }

    private fun clearScreenModel(screen: Screen) {
        ScreenModelStore.remove(screen)
        (screen as? ContainerScreen<*>)?.navigationState?.getChildScreens()?.forEach(::clearScreenModel)
    }

}