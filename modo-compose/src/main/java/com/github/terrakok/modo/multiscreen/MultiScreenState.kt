package com.github.terrakok.modo.multiscreen

import android.os.Parcelable
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen
import kotlinx.parcelize.Parcelize

typealias MultiScreenNavModel = NavModel<MultiScreenState, MultiScreenAction>

interface MultiScreenNavContainer : NavigationContainer<MultiScreenState, MultiScreenAction>

fun MultiScreenNavModel(
    screens: List<Screen>,
    selected: Int = 0
) = MultiScreenNavModel(MultiScreenState(screens, selected))

fun MultiScreenNavModel(
    vararg screens: Screen,
    selected: Int = 0
) = MultiScreenNavModel(MultiScreenState(screens.toList(), selected))

@Parcelize
data class MultiScreenState(
    val screens: List<Screen>,
    val selected: Int
) : NavigationState, Parcelable {
    override fun getChildScreens(): List<Screen> = screens
}