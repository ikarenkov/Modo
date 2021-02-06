package com.github.terrakok.modo

import android.os.Bundle

private const val STATE_SEPARATOR = "––µø∂ø––"
private const val STATE_KEY = "modo_render_state"

fun Modo.saveState(bundle: Bundle) {
    bundle.putString(STATE_KEY, saveStateToString(state))
}

fun Modo.restoreState(bundle: Bundle?) {
    state = restoreStateFromString(bundle?.getString(STATE_KEY))
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

internal fun saveStateToString(state: NavigationState): String =
    state.chain.joinToString(STATE_SEPARATOR) { it.id }

internal fun restoreStateFromString(string: String?): NavigationState =
    restoreStateFromIds(string?.split(STATE_SEPARATOR))

internal fun AppScreen(id: String) = AppScreen(id) { error("AppScreen stub!") }
internal fun restoreStateFromIds(ids: List<String>?): NavigationState {
    val chain = ids
        ?.filter { it.isNotEmpty() }
        ?.map { AppScreen(it) }
        ?: emptyList()
    return NavigationState(chain)
}