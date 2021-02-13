package com.github.terrakok.modo

data class NavigationState(
    val chain: List<Screen> = emptyList()
) {
    companion object
}
