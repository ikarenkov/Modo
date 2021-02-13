package com.github.terrakok.modo

import android.os.Bundle

private const val STATE_SEPARATOR = "––µø∂ø––"
private const val STATE_KEY = "modo_render_state"

fun Modo.saveState(bundle: Bundle) {
    bundle.putString(STATE_KEY, state.stringify())
}

fun Modo.restoreState(bundle: Bundle?) {
    state = NavigationState.parse(bundle?.getString(STATE_KEY))
}

fun Modo.init(bundle: Bundle?, firstScreen: AppScreen) {
    if (state.chain.isEmpty()) {
        if (bundle?.containsKey(STATE_KEY) == true) {
            restoreState(bundle)
        } else {
            forward(firstScreen)
        }
    }
}

internal fun NavigationState.stringify(): String =
    chain.joinToString(STATE_SEPARATOR) {
        it as? AppScreen ?: error("ModoRender works with AppScreens only!")
        it.stringify()
    }

internal fun NavigationState.Companion.parse(str: String?): NavigationState =
    restoreStateFromScreenStrings(str?.split(STATE_SEPARATOR))

internal fun restoreStateFromScreenStrings(ids: List<String>?): NavigationState {
    val chain = ids
        ?.filter { it.isNotEmpty() }
        ?.map { AppScreen.parse(it) }
        ?: emptyList()
    return NavigationState(chain)
}