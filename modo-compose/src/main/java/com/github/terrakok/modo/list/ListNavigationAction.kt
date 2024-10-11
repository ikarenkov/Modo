package com.github.terrakok.modo.list

import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.ReducerAction
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey

fun interface ListNavigationAction : ReducerAction<ListNavigationState> {

    class RemoveScreens private constructor(
        private val reducer: ReducerAction<ListNavigationState>
    ) : ListNavigationAction {

        constructor(removeCondition: (pos: Int, screen: Screen) -> Boolean) : this(
            ReducerAction { oldState ->
                ListNavigationState(
                    oldState.screens.filterIndexed { index, screen -> !removeCondition(index, screen) }
                )
            }
        )

        constructor(screenToRemove: Screen, vararg screensToRemove: Screen) : this(
            ReducerAction { oldState ->
                val screensToRemoveSet = screensToRemove.toMutableSet().apply { add(screenToRemove) }
                ListNavigationState(
                    oldState.screens.filter { screen -> screen !in screensToRemoveSet }
                )
            }
        )

        constructor(screenKeyToRemove: ScreenKey) : this(
            { _, screen ->
                screen.screenKey == screenKeyToRemove
            }
        )

        // Unable to use vararg because of https://youtrack.jetbrains.com/issue/KT-33565/Allow-vararg-parameter-of-inline-class-type
        constructor(screenKeysToRemove: Set<ScreenKey>) : this(
            ReducerAction { oldState ->
                ListNavigationState(
                    oldState.screens.filter { screen -> screen.screenKey !in screenKeysToRemove }
                )
            }
        )

        override fun reduce(oldState: ListNavigationState): ListNavigationState = reducer.reduce(oldState)

        companion object {
            inline operator fun <reified T : Screen> invoke() = RemoveScreens { _, screen -> screen is T }
        }
    }

    class AddScreens private constructor(
        private val reducer: ReducerAction<ListNavigationState>
    ) : ListNavigationAction {

        constructor(pos: Int, screen: Screen, vararg screens: Screen) : this(
            ReducerAction { oldState ->
                val newScreensCount = screens.size + 1
                ListNavigationState(
                    List(oldState.screens.size + newScreensCount) {
                        when (it) {
                            in 0 until pos -> oldState.screens[it]
                            pos -> screen
                            in pos + 1 until pos + newScreensCount -> screens[it - pos - 1]
                            else -> oldState.screens[it - newScreensCount]
                        }
                    }
                )
            }
        )

        constructor(screen: Screen, vararg screens: Screen, addToEnd: Boolean = false) : this(
            ReducerAction { oldState ->
                ListNavigationState(
                    if (addToEnd) {
                        List(oldState.screens.size + screens.size + 1) {
                            when (it) {
                                in 0 until oldState.screens.size -> oldState.screens[it]
                                oldState.screens.size -> screen
                                else -> screens[it - oldState.screens.size - 1]
                            }
                        }
                    } else {
                        List(1 + screens.size + oldState.screens.size) {
                            when (it) {
                                0 -> screen
                                in 1..screens.size -> screens[it - 1]
                                else -> oldState.screens[it - 1 - screens.size]
                            }
                        }
                    }
                )
            }
        )

        override fun reduce(oldState: ListNavigationState): ListNavigationState = reducer.reduce(oldState)
    }

    class SetScreens private constructor(
        private val reducer: ReducerAction<ListNavigationState>
    ) : ListNavigationAction {

        constructor(vararg screens: Screen) : this(
            ReducerAction { _ ->
                ListNavigationState(screens.toList())
            }
        )

        constructor(screens: List<Screen>) : this(
            ReducerAction { _ -> ListNavigationState(screens) }
        )

        override fun reduce(oldState: ListNavigationState): ListNavigationState = reducer.reduce(oldState)
    }

}

fun NavigationContainer<ListNavigationState, ListNavigationAction>.dispatch(action: (ListNavigationState) -> ListNavigationState) =
    dispatch(ListNavigationAction(action))

fun NavigationContainer<ListNavigationState, ListNavigationAction>.addScreens(pos: Int, screen: Screen, vararg screens: Screen) =
    dispatch(ListNavigationAction.AddScreens(pos, screen, *screens))

fun NavigationContainer<ListNavigationState, ListNavigationAction>.addScreens(screen: Screen, vararg screens: Screen, addToEnd: Boolean = false) =
    dispatch(ListNavigationAction.AddScreens(screen, *screens, addToEnd = addToEnd))

fun NavigationContainer<ListNavigationState, ListNavigationAction>.removeScreens(removeCondition: (pos: Int, screen: Screen) -> Boolean) =
    dispatch(ListNavigationAction.RemoveScreens(removeCondition))

fun NavigationContainer<ListNavigationState, ListNavigationAction>.removeScreen(screenKeyToRemove: ScreenKey) =
    dispatch(ListNavigationAction.RemoveScreens(screenKeyToRemove))

fun NavigationContainer<ListNavigationState, ListNavigationAction>.removeScreens(screenToRemove: Screen) =
    dispatch(ListNavigationAction.RemoveScreens(screenToRemove))

inline fun <reified T : Screen> NavigationContainer<ListNavigationState, ListNavigationAction>.removeScreens() =
    dispatch(ListNavigationAction.RemoveScreens<T>())

fun NavigationContainer<ListNavigationState, ListNavigationAction>.setScreens(vararg screens: Screen) =
    dispatch(ListNavigationAction.SetScreens(*screens))

fun NavigationContainer<ListNavigationState, ListNavigationAction>.removeAllScreens() =
    dispatch(ListNavigationAction.SetScreens())