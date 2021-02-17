package com.github.terrakok.modo

import org.junit.Test
import kotlin.test.assertTrue

class NavigationStateSerializationTest {

    @Test
    fun `navigation state serialization case #1`() {
        val state = NavigationState(listOf(A, B, C, D, E))
        val restored = NavigationState.parse(state.stringify())
        assertTrue(state.sameChain(restored))
    }

    @Test
    fun `navigation state serialization case #2`() {
        val state = NavigationState()
        val restored = NavigationState.parse(state.stringify())
        assertTrue(state.sameChain(restored))
    }

    @Test
    fun `navigation state serialization case #3`() {
        val state = NavigationState(listOf(A))
        val restored = NavigationState.parse(state.stringify())
        assertTrue(state.sameChain(restored))
    }
}