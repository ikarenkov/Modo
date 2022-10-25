package com.github.terrakok.modo

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StackReducerTest {
    private val A = Screen("A", null)
    private val B = Screen("B", null)
    private val C = Screen("C", null)
    private val D = Screen("D", null)

    @Test
    fun `test back action`() {
        val reducer = StackReducer()

        val prev1 = StackNavigationState(listOf(A, B))
        val next1 = reducer.reduce(Back, prev1)
        assertTrue(next1.sameStack(StackNavigationState(listOf(A))))

        val prev2 = StackNavigationState(listOf(A))
        val next2 = reducer.reduce(Back, prev2)
        assertTrue(next2.sameStack(StackNavigationState()))

        assertFalse(prev1.sameStack(prev2))
    }

    @Test
    fun `test exit action`() {
        val reducer = StackReducer()

        val prev1 = StackNavigationState(listOf(A, B))
        val next1 = reducer.reduce(Exit, prev1)
        assertTrue(next1.sameStack(StackNavigationState()))

        val prev2 = StackNavigationState()
        val next2 = reducer.reduce(Exit, prev2)
        assertTrue(next2.sameStack(StackNavigationState()))

        assertFalse(prev1.sameStack(prev2))
    }

    @Test
    fun `test forward action`() {
        val reducer = StackReducer()

        val prev1 = StackNavigationState(listOf(A, B))
        val next1 = reducer.reduce(Forward(C), prev1)
        assertTrue(next1.sameStack(StackNavigationState(listOf(A, B, C))))

        val prev2 = StackNavigationState()
        val next2 = reducer.reduce(Forward(A, B, C), prev2)
        assertTrue(next2.sameStack(StackNavigationState(listOf(A, B, C))))

        val prev3 = StackNavigationState(listOf(A, B, C))
        val next3 = reducer.reduce(Forward(A, B, C), prev3)
        assertTrue(next3.sameStack(StackNavigationState(listOf(A, B, C, A, B, C))))

        assertFalse(prev1.sameStack(prev3))
    }

    @Test
    fun `test replace action`() {
        val reducer = StackReducer()

        val prev1 = StackNavigationState(listOf(A, B))
        val next1 = reducer.reduce(Replace(C), prev1)
        assertTrue(next1.sameStack(StackNavigationState(listOf(A, C))))

        val prev2 = StackNavigationState()
        val next2 = reducer.reduce(Replace(A, B, C), prev2)
        assertTrue(next2.sameStack(StackNavigationState(listOf(A, B, C))))

        val prev3 = StackNavigationState(listOf(A, B, C))
        val next3 = reducer.reduce(Replace(A, B, C), prev3)
        assertTrue(next3.sameStack(StackNavigationState(listOf(A, B, A, B, C))))

        assertFalse(prev1.sameStack(prev3))
    }

    @Test
    fun `test newStack action`() {
        val reducer = StackReducer()

        val prev1 = StackNavigationState(listOf(A, B))
        val next1 = reducer.reduce(NewStack(C), prev1)
        assertTrue(next1.sameStack(StackNavigationState(listOf(C))))

        val prev2 = StackNavigationState()
        val next2 = reducer.reduce(NewStack(A, B, C), prev2)
        assertTrue(next2.sameStack(StackNavigationState(listOf(A, B, C))))

        val prev3 = StackNavigationState(listOf(A, B, C))
        val next3 = reducer.reduce(NewStack(D, B, A), prev3)
        assertTrue(next3.sameStack(StackNavigationState(listOf(D, B, A))))

        assertFalse(prev1.sameStack(prev3))
    }

    @Test
    fun `test backTo action`() {
        val reducer = StackReducer()

        val prev1 = StackNavigationState(listOf(A, B))
        val next1 = reducer.reduce(BackTo(C.id), prev1)
        assertTrue(next1.sameStack(StackNavigationState(listOf(A, B))))

        val prev2 = StackNavigationState(listOf(A, B, C))
        val next2 = reducer.reduce(BackTo(A.id), prev2)
        assertTrue(next2.sameStack(StackNavigationState(listOf(A))))

        val prev3 = StackNavigationState(listOf(A, B, C, B, D))
        val next3 = reducer.reduce(BackTo(B.id), prev3)
        assertTrue(next3.sameStack(StackNavigationState(listOf(A, B, C, B))))

        assertFalse(prev1.sameStack(prev3))
    }
}

internal fun NavigationState?.sameStack(other: StackNavigationState): Boolean {
    if (this == null) return false
    if (this !is StackNavigationState) return false
    return stack.map { it.id } == other.stack.map { it.id }
}