package com.github.terrakok.modo

import org.junit.Test
import kotlin.test.assertEquals

class ModoStateTest {

    @Test
    fun serializationTest1() {
        val state = NavigationState(listOf(A, B, C, D, E))
        val str = saveStateToString(state)
        val restored = restoreStateFromString(str)
        assertEquals(state, restored)
    }

    @Test
    fun serializationTest2() {
        val state = NavigationState()
        val str = saveStateToString(state)
        val restored = restoreStateFromString(str)
        assertEquals(state, restored)
    }

    @Test
    fun serializationTest3() {
        val state = NavigationState(listOf(A))
        val str = saveStateToString(state)
        val restored = restoreStateFromString(str)
        assertEquals(state, restored)
    }
}