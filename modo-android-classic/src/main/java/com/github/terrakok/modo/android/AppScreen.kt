package com.github.terrakok.modo.android

import androidx.fragment.app.Fragment
import com.github.terrakok.modo.Screen

/**
 * Fragment based Screen
 */
class AppScreen(
    override val id: String,
    val replacePreviousScreen: Boolean,
    val create: () -> Fragment
) : Screen {
    constructor(
        id: String,
        create: () -> Fragment
    ) : this(id, true, create)

    override fun toString() = "[$id]"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppScreen

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
