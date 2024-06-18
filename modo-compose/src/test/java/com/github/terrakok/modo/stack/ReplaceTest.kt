package com.github.terrakok.modo.stack

import com.github.terrakok.modo.MockScreen
import com.github.terrakok.modo.ScreenKey
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ReplaceTest {

    @Test
    fun `When replace empty stack - Then stack contains provided screen`() {
        val oldState = StackState(emptyList())
        val newScreen = MockScreen()
        val action = Replace(newScreen)

        val newState = action.reduce(oldState)

        assertContentEquals(expected = listOf(newScreen), actual = newState.stack)
    }

    @Test
    fun `When replace non-empty stack - Then stack contains provided screen`() {
        val oldState = StackState(listOf(MockScreen()))
        val newScreen1 = MockScreen(ScreenKey("1"))
        val newScreen2 = MockScreen(ScreenKey("2"))
        val action = Replace(newScreen1, newScreen2)

        val newState = action.reduce(oldState)

        assertContentEquals(expected = listOf(newScreen1, newScreen2), actual = newState.stack)
    }

}