package com.github.terrakok.modo

import android.os.Parcelable
import com.github.terrakok.modo.containers.ContainerScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data class MultiNavigation(
    val containers: List<ContainerScreen<*>>,
    val selected: Int
) : NavigationState, Parcelable {
    override fun getChildScreens(): List<Screen> = containers
}

class SetContainers(val state: MultiNavigation) : NavigationAction
class SelectContainer(val index: Int) : NavigationAction

fun NavigationDispatcher.setContainers(state: MultiNavigation) = dispatch(SetContainers(state))
fun NavigationDispatcher.selectContainer(index: Int) = dispatch(SelectContainer(index))

class MultiReducer : NavigationReducer<MultiNavigation> {
    override fun reduce(action: NavigationAction, state: MultiNavigation): MultiNavigation = when (action) {
        is SetContainers -> action.state
        is SelectContainer -> state.copy(selected = action.index)
        else -> state
    }
}