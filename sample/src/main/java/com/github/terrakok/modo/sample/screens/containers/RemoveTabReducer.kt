package com.github.terrakok.modo.sample.screens.containers

import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.multiscreen.MultiScreenState

/**
 * The sample of the action that is handled by reducer
 */
class RemoveTabReducer(private val pos: Int) : NavigationReducer<MultiScreenState> {

    override fun reduce(oldState: MultiScreenState): MultiScreenState = oldState.copy(
        screens = oldState.screens.filterIndexed { index, _ -> index != pos },
        selected = if (oldState.selected == pos) 0 else oldState.selected
    )

}