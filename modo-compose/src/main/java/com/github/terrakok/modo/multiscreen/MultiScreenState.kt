package com.github.terrakok.modo.multiscreen

import android.os.Parcelable
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.NavigationAction
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen
import kotlinx.parcelize.Parcelize

typealias MultiScreenNavModel = NavModel<MultiScreenState>

fun MultiScreenNavModel(
    containers: List<ContainerScreen<*>>,
    selected: Int
) = MultiScreenNavModel(MultiScreenState(containers, selected))

@Parcelize
data class MultiScreenState(
    val containers: List<ContainerScreen<*>>,
    val selected: Int
) : NavigationState, Parcelable {
    override fun getChildScreens(): List<Screen> = containers
}

class SetContainers(val state: MultiScreenState) : NavigationAction
class SelectContainer(val index: Int) : NavigationAction

fun NavigationContainer<MultiScreenState>.setContainers(state: MultiScreenState) = dispatch(SetContainers(state))
fun NavigationContainer<MultiScreenState>.selectContainer(index: Int) = dispatch(SelectContainer(index))

class MultiReducer : NavigationReducer<MultiScreenState> {
    override fun reduce(action: NavigationAction, state: MultiScreenState): MultiScreenState = when (action) {
        is SetContainers -> action.state
        is SelectContainer -> state.copy(selected = action.index)
        else -> state
    }
}