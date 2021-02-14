package com.github.terrakok.modo

import androidx.fragment.app.Fragment

/**
 * Fragment based Screen
 */
class AppScreen(
    override val id: String,
    val replacePreviousScreen: Boolean,
    val create: () -> Fragment
): Screen {
    constructor(
        id: String,
        create: () -> Fragment
    ) : this(id, true, create)

    internal fun stringify(): String =
        "$id$SEPARATOR$replacePreviousScreen"

    override fun toString() = buildString {
        append("AppScreen[$id")
        if (!replacePreviousScreen) append("+")
        append("]")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppScreen

        if (id != other.id) return false
        if (replacePreviousScreen != other.replacePreviousScreen) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + replacePreviousScreen.hashCode()
        return result
    }


    companion object {
        private const val SEPARATOR = "∞∞"
        internal fun parse(str: String): AppScreen {
            val (id, replacePreviousScreen) = str.split(SEPARATOR)
            return AppScreen(id, replacePreviousScreen.toBoolean()) {
                error("AppScreen restore stub!")
            }
        }
    }
}
