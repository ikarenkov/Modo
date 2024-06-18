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
data class ListNavigationState(
    val screens: List<Screen>
) : NavigationState {
    override fun getChildScreens(): List<Screen> = screens
}
