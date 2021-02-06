package com.github.terrakok.modo

import org.junit.Test
import kotlin.test.assertEquals

class ModoStateTest {

    @Test
    fun serializationTest1() {
        val state = NavigationState(listOf(A, B, C, D, E))
        val restored = state.stringify().asNavigationState()
        assertEquals(state, restored)
    }

    @Test
    fun serializationTest2() {
        val state = NavigationState()
        val restored = state.stringify().asNavigationState()
        assertEquals(state, restored)
    }

    @Test
    fun serializationTest3() {
        val state = NavigationState(listOf(A))
        val restored = state.stringify().asNavigationState()
        assertEquals(state, restored)
    }
}