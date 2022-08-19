package com.github.terrakok.modo

data class MultiNavigation(
    val containers: List<ContainerScreen>,
    val selected: Int
) : NavigationState

class SetContainers(val state: MultiNavigation) : NavigationAction
class SelectContainer(val index: Int) : NavigationAction

fun Navigator.setContainers(state: MultiNavigation) = dispatch(SetContainers(state))
fun Navigator.selectContainer(index: Int) = dispatch(SelectContainer(index))

class MultiReducer : NavigationReducer {
    override fun reduce(action: NavigationAction, state: NavigationState): NavigationState? {
        require(state is MultiNavigation)
        return when (action) {
            is SetContainers -> action.state
            is SelectContainer -> state.copy(selected = action.index)
            else -> null
        }
    }
}