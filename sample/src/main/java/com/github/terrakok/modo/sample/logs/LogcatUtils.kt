package com.github.terrakok.modo.sample.logs

import com.github.terrakok.modo.Screen
import logcat.logcat

internal inline fun Screen.logcat(tag: String? = null, crossinline message: () -> String) {
    logcat(tag = tag) { "${this::class.simpleName} $screenKey ${message()}" }
}