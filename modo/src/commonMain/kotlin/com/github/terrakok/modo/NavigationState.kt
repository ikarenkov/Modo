package com.github.terrakok.modo

data class NavigationState(
    val chain: List<Screen> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as NavigationState

        if (chain.map { it.id } != other.chain.map { it.id }) return false

        return true
    }

    override fun hashCode(): Int {
        return chain.hashCode()
    }
}
