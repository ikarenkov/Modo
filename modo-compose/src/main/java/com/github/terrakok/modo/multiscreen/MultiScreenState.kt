package com.github.terrakok.modo.multiscreen

import android.os.Parcelable
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen
import kotlinx.parcelize.Parcelize

typealias MultiScreenNavModel = NavModel<MultiScreenState, MultiScreenAction>

interface MultiScreenContainer : NavigationContainer<MultiScreenState, MultiScreenAction>

fun MultiScreenNavModel(
    containers: List<ContainerScreen<*, *>>,
    selected: Int
) = MultiScreenNavModel(MultiScreenState(containers, selected))

@Parcelize
data class MultiScreenState(
    val containers: List<ContainerScreen<*, *>>,
    val selected: Int
) : NavigationState, Parcelable {
    override fun getChildScreens(): List<Screen> = containers
}