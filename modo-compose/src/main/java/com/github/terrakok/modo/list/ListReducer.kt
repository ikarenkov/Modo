package com.github.terrakok.modo.list

import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey

@Deprecated("Use ListReducer instead.", ReplaceWith("ListReducer"))
typealias ListNavigationAction = ListReducer

fun interface ListReducer : NavigationReducer<ListNavigationState> {

    class RemoveScreens private constructor(
        private val reducer: NavigationReducer<ListNavigationState>
    ) : ListReducer {

        constructor(removeCondition: (pos: Int, screen: Screen) -> Boolean) : this(
            NavigationReducer { oldState ->
                ListNavigationState(
                    oldState.screens.filterIndexed { index, screen -> !removeCondition(index, screen) }
                )
            }
        )

        constructor(screenToRemove: Screen, vararg screensToRemove: Screen) : this(
            NavigationReducer { oldState ->
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
            NavigationReducer { oldState ->
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
        private val reducer: NavigationReducer<ListNavigationState>
    ) : ListReducer {

        constructor(pos: Int, screen: Screen, vararg screens: Screen) : this(
            NavigationReducer { oldState ->
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
            NavigationReducer { oldState ->
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
        private val reducer: NavigationReducer<ListNavigationState>
    ) : ListReducer {

        constructor(vararg screens: Screen) : this(
            NavigationReducer { _ ->
                ListNavigationState(screens.toList())
            }
        )

        constructor(screens: List<Screen>) : this(
            NavigationReducer { _ -> ListNavigationState(screens) }
        )

        override fun reduce(oldState: ListNavigationState): ListNavigationState = reducer.reduce(oldState)
    }

}

fun NavigationContainer<ListNavigationState>.dispatch(action: (ListNavigationState) -> ListNavigationState) =
    dispatch(NavigationReducer(action))

fun NavigationContainer<ListNavigationState>.addScreens(pos: Int, screen: Screen, vararg screens: Screen) =
    dispatch(ListReducer.AddScreens(pos, screen, *screens))

fun NavigationContainer<ListNavigationState>.addScreens(screen: Screen, vararg screens: Screen, addToEnd: Boolean = false) =
    dispatch(ListReducer.AddScreens(screen, *screens, addToEnd = addToEnd))

fun NavigationContainer<ListNavigationState>.removeScreens(removeCondition: (pos: Int, screen: Screen) -> Boolean) =
    dispatch(ListReducer.RemoveScreens(removeCondition))

fun NavigationContainer<ListNavigationState>.removeScreen(screenKeyToRemove: ScreenKey) =
    dispatch(ListReducer.RemoveScreens(screenKeyToRemove))

fun NavigationContainer<ListNavigationState>.removeScreens(screenToRemove: Screen) =
    dispatch(ListReducer.RemoveScreens(screenToRemove))

inline fun <reified T : Screen> NavigationContainer<ListNavigationState>.removeScreens() =
    dispatch(ListReducer.RemoveScreens<T>())

fun NavigationContainer<ListNavigationState>.setScreens(vararg screens: Screen) =
    dispatch(ListReducer.SetScreens(*screens))

fun NavigationContainer<ListNavigationState>.removeAllScreens() =
    dispatch(ListReducer.SetScreens())