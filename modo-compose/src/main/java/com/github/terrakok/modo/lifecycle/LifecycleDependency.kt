package com.github.terrakok.modo.lifecycle

import com.github.terrakok.modo.Screen

/**
 * Interface for lifecycle dependent objects, on witch we want to call [onPreDispose] just before actual removal of dependency and screen,
 * to be able to send ON_DISPOSE event and collect it in [Screen.Content].
 */
interface LifecycleDependency {
    fun onPreDispose()
}