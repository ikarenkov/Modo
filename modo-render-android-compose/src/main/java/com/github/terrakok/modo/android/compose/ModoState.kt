package com.github.terrakok.modo.android.compose

import android.os.Bundle
import android.util.Log
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.forward

private var modoInitialized: Boolean = false

fun Modo.init(bundle: Bundle?, firstScreenFactory: () -> ComposeScreen) {
    if (bundle == null) {
        if (!modoInitialized) {
            Log.d("Modo", "Activity first launch")
            modoInitialized = true
        } else {
            Log.d("Modo", "Activity re-launch")
        }
        forward(firstScreenFactory())
    } else {
        if (!modoInitialized) {
            Log.d("Modo", "Activity restored after process death")
            modoInitialized = true
            restoreScreenCounter(bundle.getInt(KEY_NAVIGATION_SCREEN_COUNTER)!!)
            val restoredState = bundle.restoreNavigationState()
            if (restoredState.chain.isNotEmpty()) {
                restore(restoredState)
            }
        } else {
            Log.d("Modo", "Activity restored after rotation")
            //do nothing
        }
    }
}

private const val KEY_NAVIGATION_STATE = "KEY_NAVIGATION_STATE"
private const val KEY_NAVIGATION_SCREEN_COUNTER = "KEY_NAVIGATION_SCREEN_COUNTER"
private fun Bundle.restoreNavigationState() = NavigationState(
    getParcelableArray(KEY_NAVIGATION_STATE)
        ?.toList()
        .orEmpty()
        .filterIsInstance<ComposeScreen>()
)

fun Modo.saveState(bundle: Bundle) {
    bundle.putParcelableArray(
        KEY_NAVIGATION_STATE,
        state.chain.filterIsInstance<ComposeScreen>().toTypedArray()
    )
    bundle.putInt(KEY_NAVIGATION_SCREEN_COUNTER, screenCounterKey.get())
}