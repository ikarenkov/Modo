package com.github.terrakok.modo

data class MultiNavigation(
    val containers: List<ContainerScreen>,
    val activeContainerIndex: Int
) : NavigationState

class SetContainers(val state: MultiNavigation) : NavigationAction
class SelectContainer(val index: Int) : NavigationAction

fun NavigationDispatcher.setContainers(state: MultiNavigation) = dispatch(SetContainers(state))
fun NavigationDispatcher.selectContainer(index: Int) = dispatch(SelectContainer(index))

class MultiReducer : NavigationReducer {
    override fun reduce(action: NavigationAction, state: NavigationState): NavigationState? {
        if (state !is MultiNavigation) return null
        return when (action) {
            is SetContainers -> action.state
            is SelectContainer -> state.copy(activeContainerIndex = action.index)
            else -> null
        }
    }
}