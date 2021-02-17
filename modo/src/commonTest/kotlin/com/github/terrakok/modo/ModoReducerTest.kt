package com.github.terrakok.modo

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModoReducerTest {

    private class S(override val id: String) : Screen
    private val A = S("A")
    private val B = S("B")
    private val C = S("C")
    private val D = S("D")

    @Test
    fun `test back action`() {
        val reducer = ModoReducer()

        val prev1 = NavigationState(listOf(A, B))
        val next1 = reducer(Back, prev1)
        assertTrue(next1.sameChain(NavigationState(listOf(A))))

        val prev2 = NavigationState(listOf(A))
        val next2 = reducer(Back, prev2)
        assertTrue(next2.sameChain(NavigationState()))

        assertFalse(prev1.sameChain(prev2))
    }

    @Test
    fun `test exit action`() {
        val reducer = ModoReducer()

        val prev1 = NavigationState(listOf(A, B))
        val next1 = reducer(Exit, prev1)
        assertTrue(next1.sameChain(NavigationState()))

        val prev2 = NavigationState()
        val next2 = reducer(Exit, prev2)
        assertTrue(next2.sameChain(NavigationState()))

        assertFalse(prev1.sameChain(prev2))
    }

    @Test
    fun `test forward action`() {
        val reducer = ModoReducer()

        val prev1 = NavigationState(listOf(A, B))
        val next1 = reducer(Forward(C), prev1)
        assertTrue(next1.sameChain(NavigationState(listOf(A, B, C))))

        val prev2 = NavigationState()
        val next2 = reducer(Forward(A, B, C), prev2)
        assertTrue(next2.sameChain(NavigationState(listOf(A, B, C))))

        val prev3 = NavigationState(listOf(A, B, C))
        val next3 = reducer(Forward(A, B, C), prev3)
        assertTrue(next3.sameChain(NavigationState(listOf(A, B, C, A, B, C))))

        assertFalse(prev1.sameChain(prev3))
    }

    @Test
    fun `test replace action`() {
        val reducer = ModoReducer()

        val prev1 = NavigationState(listOf(A, B))
        val next1 = reducer(Replace(C), prev1)
        assertTrue(next1.sameChain(NavigationState(listOf(A, C))))

        val prev2 = NavigationState()
        val next2 = reducer(Replace(A, B, C), prev2)
        assertTrue(next2.sameChain(NavigationState(listOf(A, B, C))))

        val prev3 = NavigationState(listOf(A, B, C))
        val next3 = reducer(Replace(A, B, C), prev3)
        assertTrue(next3.sameChain(NavigationState(listOf(A, B, A, B, C))))

        assertFalse(prev1.sameChain(prev3))
    }

    @Test
    fun `test new stack action`() {
        val reducer = ModoReducer()

        val prev1 = NavigationState(listOf(A, B))
        val next1 = reducer(NewStack(C), prev1)
        assertTrue(next1.sameChain(NavigationState(listOf(C))))

        val prev2 = NavigationState()
        val next2 = reducer(NewStack(A, B, C), prev2)
        assertTrue(next2.sameChain(NavigationState(listOf(A, B, C))))

        val prev3 = NavigationState(listOf(A, B, C))
        val next3 = reducer(NewStack(D, B, A), prev3)
        assertTrue(next3.sameChain(NavigationState(listOf(D, B, A))))

        assertFalse(prev1.sameChain(prev3))
    }

    @Test
    fun `test back to action`() {
        val reducer = ModoReducer()

        val prev1 = NavigationState(listOf(A, B))
        val next1 = reducer(BackTo(C.id), prev1)
        assertTrue(next1.sameChain(NavigationState(listOf(A, B))))

        val prev2 = NavigationState(listOf(A, B, C))
        val next2 = reducer(BackTo(A.id), prev2)
        assertTrue(next2.sameChain(NavigationState(listOf(A))))

        val prev3 = NavigationState(listOf(A, B, C, B, D))
        val next3 = reducer(BackTo(B.id), prev3)
        assertTrue(next3.sameChain(NavigationState(listOf(A, B, C, B))))

        assertFalse(prev1.sameChain(prev3))
    }
}

internal fun NavigationState.sameChain(other: NavigationState) =
    chain.map { it.id } == other.chain.map { it.id }