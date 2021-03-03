package com.github.terrakok.modo.android.compose

import android.os.Bundle
import android.util.Log
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.forward

private var modoInitialized: Boolean = false

fun Modo.init(bundle: Bundle?, render: ComposeRender, firstScreen: ComposeScreen) {
    if (bundle == null) {
        if (!modoInitialized) {
            Log.d("Modo", "Activity first launch")
            modoInitialized = true
        } else {
            Log.d("Modo", "Activity re-launch")
        }
        forward(firstScreen)
    } else {
        if (!modoInitialized) {
            Log.d("Modo", "Activity restored after process death")
            modoInitialized = true
            // TODO think how to restore state after process death
            forward(firstScreen)
            // restore(render.currentState)
        } else {
            Log.d("Modo", "Activity restored after rotation")
            //do nothing
        }
    }
}