package com.github.terrakok.modo

import org.junit.Test
import kotlin.test.assertEquals

class ModoRenderTest {

    @Test
    fun diffTest1() {
        val diff = ModoRender.diff(
            NavigationState(),
            NavigationState()
        )
        assertEquals(emptyList(), diff)
    }

    @Test
    fun diffTest2() {
        val diff = ModoRender.diff(
            NavigationState(listOf(A, B, C)),
            NavigationState()
        )
        assertEquals(listOf(Pop(3)), diff)
    }

    @Test
    fun diffTest3() {
        val diff = ModoRender.diff(
            NavigationState(),
            NavigationState(listOf(A, B, C))
        )
        assertEquals(listOf(Push(listOf(A, B, C))), diff)
    }

    @Test
    fun diffTest4() {
        val diff = ModoRender.diff(
            NavigationState(listOf(A, B, C)),
            NavigationState(listOf(A, B, D, E))
        )
        assertEquals(listOf(Pop(1), Push(listOf(D, E))), diff)
    }

    @Test
    fun diffTest5() {
        val diff = ModoRender.diff(
            NavigationState(listOf(A, B)),
            NavigationState(listOf(A, B, D, E))
        )
        assertEquals(listOf(Push(listOf(D, E))), diff)
    }

    @Test
    fun diffTest6() {
        val diff = ModoRender.diff(
            NavigationState(listOf(A, B, C, D, E)),
            NavigationState(listOf(A, B))
        )
        assertEquals(listOf(Pop(3)), diff)
    }
}