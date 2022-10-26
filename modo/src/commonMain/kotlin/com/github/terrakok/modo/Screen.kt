package com.github.terrakok.modo

import dev.icerock.moko.parcelize.Parcelable

interface Screen : Parcelable {
    val screenName: String get() = "Screen"
}

abstract class ContainerScreen<State : NavigationState>(
    initialState: State,
) : Screen, NavigationDispatcher {

    abstract val reducer: NavigationReducer<State>

    open var navigationState: State = initialState
        set(value) {
            field = value
            renderer?.render(value)
        }

    var renderer: NavigationRenderer? = null
        set(value) {
            field = value
            value?.render(navigationState)
        }

    override fun dispatch(action: NavigationAction) {
        navigationState = reducer.reduce(action, navigationState)
    }

}