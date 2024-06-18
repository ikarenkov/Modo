package com.github.terrakok.modo.stack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.MockScreen
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import kotlinx.parcelize.Parcelize
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test
import kotlin.test.assertContentEquals

class BackToTest {

    @Test
    fun `When back to condition always false - Then stack not changed`() {
        val oldState = StackState(listOf(MockScreen(), MockScreen()))
        val action = BackTo { _, _ -> false }

        val newState = action.reduce(oldState)

        assertEquals(oldState, newState)
    }

    @Test
    fun `When including and back to condition always false - Then stack not changed`() {
        val oldState = StackState(listOf(MockScreen(), MockScreen()))
        val action = BackTo(including = true) { _, _ -> false }

        val newState = action.reduce(oldState)

        assertEquals(oldState, newState)
    }

    @Test
    fun `When back to first including - Then stack is empty`() {
        val oldState = StackState(listOf(MockScreen(), MockScreen()))
        val action = BackTo(including = true) { pos, _ -> pos == 0 }

        val newState = action.reduce(oldState)

        assertEquals(StackState(emptyList()), newState)
    }

    @Test
    fun `When back to AnotherMockScreen when it on the top - Then no changes`() {
        val oldState = StackState(listOf(MockScreen(), AnotherMockScreen(), AnotherMockScreen()))
        val action = BackTo<AnotherMockScreen>()

        val newState = action.reduce(oldState)

        assertEquals(oldState, newState)
    }

    @Test
    fun `When back to AnotherMockScreen when it on the top and including - Then remove last screen`() {
        val oldStack = listOf(MockScreen(), AnotherMockScreen(), AnotherMockScreen())
        val oldState = StackState(oldStack)
        val action = BackTo<AnotherMockScreen>(including = true)

        val newState = action.reduce(oldState)

        assertContentEquals(oldStack.dropLast(1), newState.stack)
    }

    @Test
    fun `When back to on empty stack - Then no changes`() {
        val oldState = StackState(emptyList())
        val action = BackTo<AnotherMockScreen>()

        val newState = action.reduce(oldState)

        assertEquals(oldState, newState)
    }

    @Test
    fun `When back to on empty stack including - Then no changes`() {
        val oldState = StackState(emptyList())
        val action = BackTo<AnotherMockScreen>(including = true)

        val newState = action.reduce(oldState)

        assertEquals(oldState, newState)
    }

    @Parcelize
    class AnotherMockScreen(
        override val screenKey: ScreenKey = generateScreenKey()
    ) : Screen {

        @Composable
        override fun Content(modifier: Modifier) = Unit
    }

}