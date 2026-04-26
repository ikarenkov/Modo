package com.github.terrakok.modo.sample.screens.containers

import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.multiscreen.MultiScreenState

fun NavigationContainer<MultiScreenState>.addTab(
    id: String,
    rootScreen: Screen
) = dispatch { oldState ->
    MultiScreenState(
        oldState.screens + SampleStack(rootScreen),
        oldState.selected
    )
}