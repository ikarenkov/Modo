package com.github.terrakok.modo.list

import com.github.terrakok.modo.MockScreen
import com.github.terrakok.modo.ScreenKey
import kotlin.test.Test
import kotlin.test.assertEquals

class ListNavigationActionAddScreensTest {

    @Test
    fun `When add screen to empty list - Then screen is added`() {
        val screen = MockScreen(ScreenKey("1"))
        val oldState = ListNavigationState(emptyList())
        val action = ListNavigationAction.AddScreens(screen)

        val newState = action.reduce(oldState)

        assertEquals(
            expected = listOf(screen),
            actual = newState.screens
        )
    }

    @Test
    fun `When add screen to empty list by pos - Then screen is added`() {
        val screen = MockScreen(ScreenKey("1"))
        val oldState = ListNavigationState(emptyList())
        val action = ListNavigationAction.AddScreens(pos = 0, screen)

        val newState = action.reduce(oldState)

        assertEquals(
            expected = listOf(screen),
            actual = newState.screens
        )
    }

    @Test
    fun `When add screen by pos to the end - Then screen is added`() {
        val screen1 = MockScreen(ScreenKey("1"))
        val screen2 = MockScreen(ScreenKey("2"))
        val oldState = ListNavigationState(listOf(screen1))
        val action = ListNavigationAction.AddScreens(pos = 1, screen2)

        val newState = action.reduce(oldState)

        assertEquals(
            expected = listOf(screen1, screen2),
            actual = newState.screens
        )
    }

    @Test
    fun `When add screens - Then screens are added`() {
        val screen1 = MockScreen(ScreenKey("1"))
        val screen2 = MockScreen(ScreenKey("2"))
        val screen3 = MockScreen(ScreenKey("3"))
        val oldState = ListNavigationState(emptyList())
        val action = ListNavigationAction.AddScreens(screen1, screen2, screen3)

        val newState = action.reduce(oldState)

        assertEquals(
            expected = listOf(screen1, screen2),
            actual = newState.screens
        )
    }

    @Test
    fun `When add screens by position - Then screens are added`() {
        val screen1 = MockScreen(ScreenKey("1"))
        val screen2 = MockScreen(ScreenKey("2"))
        val screen3 = MockScreen(ScreenKey("3"))
        val screen4 = MockScreen(ScreenKey("4"))
        val screen5 = MockScreen(ScreenKey("5"))
        val oldState = ListNavigationState(listOf(screen1, screen5))
        val action = ListNavigationAction.AddScreens(pos = 1, screen2, screen3, screen4)

        val newState = action.reduce(oldState)

        assertEquals(
            expected = listOf(screen1, screen2, screen3, screen4, screen5),
            actual = newState.screens
        )
    }

    @Test
    fun `When add screens by position to empty list - Then screens are added`() {
        val screen1 = MockScreen(ScreenKey("1"))
        val screen2 = MockScreen(ScreenKey("2"))
        val screen3 = MockScreen(ScreenKey("3"))
        val oldState = ListNavigationState(emptyList())
        val action = ListNavigationAction.AddScreens(pos = 0, screen1, screen2, screen3)

        val newState = action.reduce(oldState)

        assertEquals(
            expected = listOf(screen1, screen2, screen3),
            actual = newState.screens
        )
    }

    @Test
    fun `When add screens to the end in empty state - Then screens are added`() {
        val screen1 = MockScreen(ScreenKey("1"))
        val screen2 = MockScreen(ScreenKey("2"))
        val screen3 = MockScreen(ScreenKey("3"))
        val oldState = ListNavigationState(listOf())
        val action = ListNavigationAction.AddScreens(screen1, screen2, screen3, addToEnd = true)

        val newState = action.reduce(oldState)

        assertEquals(
            expected = listOf(screen1, screen2, screen3),
            actual = newState.screens
        )
    }

    @Test
    fun `When add screens to the start in empty state - Then screens are added`() {
        val screen1 = MockScreen(ScreenKey("1"))
        val screen2 = MockScreen(ScreenKey("2"))
        val screen3 = MockScreen(ScreenKey("3"))
        val oldState = ListNavigationState(listOf())
        val action = ListNavigationAction.AddScreens(screen1, screen2, screen3)

        val newState = action.reduce(oldState)

        assertEquals(
            expected = listOf(screen1, screen2, screen3),
            actual = newState.screens
        )
    }

    @Test
    fun `When add screens to the end - Then screens are added`() {
        val screen1 = MockScreen(ScreenKey("1"))
        val screen2 = MockScreen(ScreenKey("2"))
        val screen3 = MockScreen(ScreenKey("3"))
        val oldState = ListNavigationState(listOf(screen1))
        val action = ListNavigationAction.AddScreens(screen2, screen3, addToEnd = true)

        val newState = action.reduce(oldState)

        assertEquals(
            expected = listOf(screen1, screen2, screen3),
            actual = newState.screens
        )
    }

    @Test
    fun `When add screens - Then screens are added to start`() {
        val screen1 = MockScreen(ScreenKey("1"))
        val screen2 = MockScreen(ScreenKey("2"))
        val screen3 = MockScreen(ScreenKey("3"))
        val oldState = ListNavigationState(listOf(screen3))
        val action = ListNavigationAction.AddScreens(screen1, screen2)

        val newState = action.reduce(oldState)

        assertEquals(
            expected = listOf(screen1, screen2, screen3),
            actual = newState.screens
        )
    }

}