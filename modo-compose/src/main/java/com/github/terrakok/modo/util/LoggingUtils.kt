package com.github.terrakok.modo.util

import android.util.Log
import com.github.terrakok.modo.Screen

fun Screen.log(text: String) {
    Log.d("LifecycleDebug", "${this::class.simpleName} ${screenKey} $text")
}