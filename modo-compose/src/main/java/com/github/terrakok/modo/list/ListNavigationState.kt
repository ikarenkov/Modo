package com.github.terrakok.modo.list

import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.ReducerAction
import com.github.terrakok.modo.Screen
import kotlinx.parcelize.Parcelize

typealias ListNavModel = NavModel<ListNavigationState, ListNavigationAction>

fun ListNavModel(screens: List<Screen>): ListNavModel = NavModel(ListNavigationState(screens = screens))

@Parcelize
class ListNavigationState(
    val screens: List<Screen>
) : NavigationState {
    override fun getChildScreens(): List<Screen> = screens
}

fun interface ListNavigationAction : ReducerAction<ListNavigationState>

typealias ListNavigationContainer = NavigationContainer<ListNavigationState, ListNavigationAction>

fun ListNavigationContainer.dispatch(updateLambda: (ListNavigationState) -> ListNavigationState) =
    dispatch(ListNavigationAction { updateLambda(it) })

