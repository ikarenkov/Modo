package com.github.terrakok.modo

import android.os.Bundle
import android.util.Log

private var modoInitialized: Boolean = false
fun Modo.init(bundle: Bundle?, render: ModoRender, firstScreen: AppScreen) {
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
            restore(render.currentState)
        } else {
            Log.d("Modo", "Activity restored after rotation")
            //do nothing
        }
    }
}