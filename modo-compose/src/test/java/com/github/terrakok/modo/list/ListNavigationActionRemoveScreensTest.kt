package com.github.terrakok.modo.list

import com.github.terrakok.modo.MockScreen
import com.github.terrakok.modo.ScreenKey
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ListNavigationActionRemoveScreensTest {

    @Test
    fun `When remove screen by key - Then screen is removed`() {
        val screen = MockScreen(ScreenKey("2"))
        val oldState = ListNavigationState(listOf(MockScreen(ScreenKey("1")), screen))
        val action = ListNavigationAction.RemoveScreens(ScreenKey("1"))

        val newState = action.reduce(oldState)

        assertContentEquals(
            expected = listOf(screen),
            actual = newState.screens
        )
    }

    @Test
    fun `When remove list of screens - Then screens are removed`() {
        val screen1 = MockScreen(ScreenKey("1"))
        val screen2 = MockScreen(ScreenKey("2"))
        val screen3 = MockScreen(ScreenKey("3"))
        val screen4 = MockScreen(ScreenKey("3"))
        val screen5 = MockScreen(ScreenKey("3"))
        val oldState = ListNavigationState(listOf(screen1, screen2, screen3, screen4))
        val action = ListNavigationAction.RemoveScreens(screen1, screen3, screen5)

        val newState = action.reduce(oldState)

        assertContentEquals(
            expected = listOf(screen2, screen4),
            actual = newState.screens
        )
    }

    @Test
    fun `When remove screen by condition - Then screen is removed`() {
        val screen1 = MockScreen(ScreenKey("1"))
        val screen2 = MockScreen(ScreenKey("2"))
        val screen3 = MockScreen(ScreenKey("3"))
        val oldState = ListNavigationState(listOf(screen1, screen2, screen3))
        val action = ListNavigationAction.RemoveScreens { _, screen -> screen.screenKey.value == "2" }

        val newState = action.reduce(oldState)

        assertContentEquals(
            expected = listOf(screen1, screen3),
            actual = newState.screens
        )
    }

    @Test
    fun `When remove screens by screen key set - Then screens are removed`() {
        val screen1 = MockScreen(ScreenKey("1"))
        val screen2 = MockScreen(ScreenKey("2"))
        val screen3 = MockScreen(ScreenKey("3"))
        val oldState = ListNavigationState(listOf(screen1, screen2, screen3))
        val action = ListNavigationAction.RemoveScreens(setOf(ScreenKey("1"), ScreenKey("3")))

        val newState = action.reduce(oldState)

        assertContentEquals(
            expected = listOf(screen2),
            actual = newState.screens
        )
    }

    @Test
    fun `When remove screen by type - Then screen is removed`() {
        val screen1 = MockScreen(ScreenKey("1"))
        val screen2 = MockScreen(ScreenKey("2"))
        val screen3 = MockScreen(ScreenKey("3"))
        val oldState = ListNavigationState(listOf(screen1, screen2, screen3))
        val action = ListNavigationAction.RemoveScreens<MockScreen>()

        val newState = action.reduce(oldState)

        assertContentEquals(
            expected = emptyList(),
            actual = newState.screens
        )
    }

}