package com.github.terrakok.modo.stack

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen
import kotlinx.parcelize.Parcelize

typealias StackNavModel = NavModel<StackState>

fun StackNavModel(stack: List<Screen>): StackNavModel = NavModel(StackState(stack))
fun StackNavModel(screen: Screen): StackNavModel = NavModel(StackState(listOf(screen)))
fun StackNavModel(vararg screens: Screen): StackNavModel = NavModel(StackState(screens.toList()))

@Stable
interface StackNavContainer : NavigationContainer<StackState>

@Parcelize
data class StackState(
    val stack: List<Screen> = emptyList(),
) : NavigationState, Parcelable {

    constructor(vararg screensStack: Screen) : this(screensStack.toList())

    override fun getChildScreens(): List<Screen> = stack

}