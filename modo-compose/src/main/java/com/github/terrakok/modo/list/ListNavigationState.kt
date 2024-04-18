package com.github.terrakok.modo.list

import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.ReducerAction
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import kotlinx.parcelize.Parcelize

typealias ListNavModel = NavModel<ListNavigationState, ListNavigationAction>

fun ListNavModel(screens: List<Screen>): ListNavModel = NavModel(ListNavigationState(screens = screens))

@Parcelize
class ListNavigationState(
    val screens: List<Screen>
) : NavigationState {
    override fun getChildScreens(): List<Screen> = screens
}

fun interface ListNavigationAction : ReducerAction<ListNavigationState> {

    class Remove(private val removeCondition: (pos: Int, screen: Screen) -> Boolean) : ListNavigationAction {

        constructor(screenToRemove: Screen) : this({ _, screen -> screen == screenToRemove })

        constructor(screenKeyToRemove: ScreenKey) : this({ _, screen -> screen.screenKey == screenKeyToRemove })

        override fun reduce(oldState: ListNavigationState): ListNavigationState = ListNavigationState(
            oldState.screens.filterIndexed { index, screen -> !removeCondition(index, screen) }
        )
    }

    class Add private constructor(private val nullablePos: Int?, private val screen: Screen) : ListNavigationAction {

        constructor(pos: Int, screen: Screen) : this(nullablePos = pos, screen = screen)
        constructor(screen: Screen) : this(nullablePos = null, screen = screen)

        override fun reduce(oldState: ListNavigationState): ListNavigationState = ListNavigationState(
            oldState.screens.toMutableList().apply {
                if (nullablePos == null) {
                    add(screen)
                } else {
                    add(nullablePos, screen)
                }
            }
        )
    }

}

typealias ListNavigationContainer = NavigationContainer<ListNavigationState, ListNavigationAction>

fun ListNavigationContainer.dispatch(updateLambda: (ListNavigationState) -> ListNavigationState) =
    dispatch(ListNavigationAction { updateLambda(it) })

