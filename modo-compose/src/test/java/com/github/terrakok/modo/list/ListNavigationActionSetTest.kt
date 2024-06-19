package com.github.terrakok.modo.list

import com.github.terrakok.modo.MockScreen
import com.github.terrakok.modo.ScreenKey
import kotlin.test.Test
import kotlin.test.assertEquals

class ListNavigationActionSetTest {

    @Test
    fun `When set screens - Then screens are set`() {
        val screen1 = MockScreen(ScreenKey("1"))
        val screen2 = MockScreen(ScreenKey("2"))
        val screen3 = MockScreen(ScreenKey("3"))
        val oldState = ListNavigationState(listOf(screen1, screen2))
        val action = ListNavigationAction.SetScreens(screen3)

        val newState = action.reduce(oldState)

        assertEquals(
            expected = listOf(screen3),
            actual = newState.screens
        )
    }

    @Test
    fun `When set screens as list - Then screens are set`() {
        val screen1 = MockScreen(ScreenKey("1"))
        val screen2 = MockScreen(ScreenKey("2"))
        val screen3 = MockScreen(ScreenKey("3"))
        val oldState = ListNavigationState(listOf(screen1, screen2))
        val action = ListNavigationAction.SetScreens(listOf(screen3))

        val newState = action.reduce(oldState)

        assertEquals(
            expected = listOf(screen3),
            actual = newState.screens
        )
    }

}