package com.github.terrakok.modo

/**
 * Holder of current navigation state
 */
data class NavigationState(
    val chain: List<Screen> = emptyList()
) {
    //for extensions
    companion object
}
