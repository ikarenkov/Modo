package com.github.terrakok.modo.android

import android.os.Parcelable
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.github.terrakok.modo.MultiScreenState
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.android.multi.FragmentMultiScreen

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
) = FragmentMultiScreen(
    id,
    MultiScreenState(
        List(roots.size) { i -> NavigationState(listOf(roots[i])) },
        selected
    )
)

fun FlowAppScreen(
    id: String,
    root: AppScreen
) = FragmentMultiScreen(
    id,
    MultiScreenState(
        listOf(NavigationState(listOf(root))),
        0
    )
)

abstract class MultiStackFragment : Fragment {
    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    abstract fun applyMultiState(multiScreenState: MultiScreenState)
    abstract fun getCurrentFragment(): Fragment?
}