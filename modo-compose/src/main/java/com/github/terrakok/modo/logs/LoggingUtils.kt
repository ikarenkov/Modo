package com.github.terrakok.modo.logs

import com.github.terrakok.modo.Screen

internal inline fun Screen.devLogV(tag: String? = "ModoDevLogs", crossinline message: () -> String) {
    DevLog.v(tag) { "${this::class.simpleName} $screenKey ${message()}" }
}

internal inline fun Screen.devLogD(tag: String? = "ModoDevLogs", crossinline message: () -> String) {
    DevLog.d(tag) { "${this::class.simpleName} $screenKey ${message()}" }
}

internal inline fun Screen.devLogI(tag: String? = "ModoDevLogs", crossinline message: () -> String) {
    DevLog.i(tag) { "${this::class.simpleName} $screenKey ${message()}" }
}