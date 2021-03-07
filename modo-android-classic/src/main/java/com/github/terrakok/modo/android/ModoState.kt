package com.github.terrakok.modo.android

import android.os.Bundle
import android.util.Log
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.forward

private var modoInitialized: Boolean = false
fun Modo.init(bundle: Bundle?, render: ModoRender, firstScreen: Screen) {
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