package com.github.terrakok.modo.list

import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen
import kotlinx.parcelize.Parcelize

typealias ListNavModel = NavModel<ListNavigationState, ListNavigationAction>

fun ListNavModel(screens: List<Screen>): ListNavModel = NavModel(ListNavigationState(screens = screens))

interface ListNavigationContainer : NavigationContainer<ListNavigationState, ListNavigationAction>

abstract class ListNavigationContainerScreen(
    navModel: ListNavModel
) : ListNavigationContainer, ContainerScreen<ListNavigationState, ListNavigationAction>(navModel)

@Parcelize
data class ListNavigationState(
    val screens: List<Screen>
) : NavigationState {
    override fun getChildScreens(): List<Screen> = screens
}
