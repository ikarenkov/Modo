package com.github.terrakok.modo

import org.junit.Test
import kotlin.test.assertEquals

class AndroidDiffTest {

    @Test
    fun `diff case #1`() {
        val diff = ModoRender.diff(
            NavigationState(),
            NavigationState()
        )
        assertEquals(emptyList(), diff)
    }

    @Test
    fun `diff case #2`() {
        val diff = ModoRender.diff(
            NavigationState(listOf(A, B, C)),
            NavigationState()
        )
        assertEquals(listOf(Pop(3)), diff)
    }

    @Test
    fun `diff case #3`() {
        val diff = ModoRender.diff(
            NavigationState(),
            NavigationState(listOf(A, B, C))
        )
        assertEquals(listOf(Push(listOf(A, B, C))), diff)
    }

    @Test
    fun `diff case #4`() {
        val diff = ModoRender.diff(
            NavigationState(listOf(A, B, C)),
            NavigationState(listOf(A, B, D, E))
        )
        assertEquals(listOf(Pop(1), Push(listOf(D, E))), diff)
    }

    @Test
    fun `diff case #5`() {
        val diff = ModoRender.diff(
            NavigationState(listOf(A, B)),
            NavigationState(listOf(A, B, D, E))
        )
        assertEquals(listOf(Push(listOf(D, E))), diff)
    }

    @Test
    fun `diff case #6`() {
        val diff = ModoRender.diff(
            NavigationState(listOf(A, B, C, D, E)),
            NavigationState(listOf(A, B))
        )
        assertEquals(listOf(Pop(3)), diff)
    }

    @Test
    fun `diff for equal states must be same`() {
        val diff1 = ModoRender.diff(
            NavigationState(listOf(A, B, C, D, E)),
            NavigationState(listOf(A, B))
        )
        val diff2 = ModoRender.diff(
            NavigationState(listOf(A, B, C, D, E)),
            NavigationState(listOf(A, B))
        )
        assertEquals(diff1, diff2)
    }
}