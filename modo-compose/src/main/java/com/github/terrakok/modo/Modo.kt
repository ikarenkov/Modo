package com.github.terrakok.modo

import android.os.Bundle

object Modo {

    private const val MODO_SCREEN_COUNTER_KEY = "MODO_SCREEN_COUNTER_KEY"
    private const val MODO_GRAPH = "MODO_GRAPH"

    fun saveInstanceState(outState: Bundle, rootScreen: Screen) {
        outState.putInt(MODO_SCREEN_COUNTER_KEY, screenCounterKey.get())
        outState.putParcelable(MODO_GRAPH, rootScreen)
    }

    /**
     * @param savedState
     * @param rootScreenProvider invokes whe saved state is null and we need to provide root screen
     */
    fun <T: Screen> restoreInstanceIfCan(savedState: Bundle?, rootScreenProvider: () -> T): T =
        if (savedState != null) {
            restoreScreenCounter(savedState.getInt(MODO_SCREEN_COUNTER_KEY))
            savedState.getParcelable<Screen>(MODO_GRAPH) as T
        } else {
            rootScreenProvider()
        }

}