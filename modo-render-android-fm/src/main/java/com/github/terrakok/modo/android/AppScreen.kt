package com.github.terrakok.modo.android

import android.os.Parcelable
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.github.terrakok.modo.MultiScreen
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen

/**
 * Fragment based Screen
 */
abstract class AppScreen(
    override val id: String,
    val replacePreviousScreen: Boolean = true
) : Screen, Parcelable {
    abstract fun create(): Fragment
    override fun toString() = "[$id]"
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

fun FlowAppScreen(
    id: String,
    root: AppScreen
) = MultiScreen(
    id,
    listOf(NavigationState(listOf(root))),
    0
)

abstract class MultiStackFragment : Fragment {
    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    abstract fun applyMultiState(multiScreen: MultiScreen)
    abstract fun getCurrentFragment(): Fragment?
}