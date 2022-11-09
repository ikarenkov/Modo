package com.github.terrakok.modo

import android.os.Parcelable

interface NavigationState : Parcelable {
    fun getChildScreens(): List<Screen>
}

interface NavigationAction

interface NavigationReducer<State : NavigationState> {
    fun reduce(action: NavigationAction, state: State): State
}

interface NavigationContainer<State : NavigationState> {
    val navigationState: State

    fun dispatch(action: NavigationAction)
}

interface NavigationRenderer<State : NavigationState> {
    fun render(state: State)
}
