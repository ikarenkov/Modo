package com.github.terrakok.modo.lifecycle

import com.github.terrakok.modo.Screen

/**
 * Interface for lifecycle-dependent objects, on witch we want to call [onPreDispose] just before actual removal of dependency and screen,
 * to be able to send ON_DISPOSE event and collect it in [Screen.Content].
 */
interface LifecycleDependency {

    /**
     * Should be called when the associated screen is ready to be moved to ON_RESUME state.
     * F.e. when screen appearance animation is finished and the screen is fully visible.
     */
    fun showTransitionFinished()

    /**
     * Should be called when the associated screen is ready to be moved to ON_PAUSE state.
     * F.e. when screen hide animation is started and the screen is not fully visible.
     */
    fun hideTransitionStarted()

    fun onPreDispose()

    companion object {
        const val KEY = "LifecycleDependency"
    }
}