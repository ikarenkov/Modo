package com.github.terrakok.modo.android

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.github.terrakok.modo.MultiScreen
import com.github.terrakok.modo.NavigationState
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

fun MultiAppScreen(
    id: String,
    roots: List<AppScreen>,
    selected: Int
) = MultiScreen(
    id,
    List(roots.size) { i -> NavigationState(listOf(roots[i])) },
    selected
)

abstract class MultiStackFragment : Fragment {
    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    abstract fun applyMultiState(multiScreen: MultiScreen)
    abstract fun getCurrentFragment(): Fragment?
}