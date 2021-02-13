package com.github.terrakok.modo

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AppScreenSerializationTest {

    @Test
    fun `restored screen must be equal original screen`() {
        val screenA = AppScreen("A", true) {
            error("AppScreen stub!")
        }
        val restoredA = AppScreen.parse(screenA.stringify())
        assertEquals(screenA, restoredA)

        val screenB = AppScreen("B", false) {
            error("AppScreen stub!")
        }
        val restoredB = AppScreen.parse(screenB.stringify())
        assertEquals(screenB, restoredB)
    }

    @Test
    fun `restored screen mustn't be equal other screen`() {
        val screenB = AppScreen("B", true) {
            error("AppScreen stub!")
        }
        val screenB2 = AppScreen("B", false) {
            error("AppScreen stub!")
        }
        val restoredScreenB = AppScreen.parse(screenB.stringify())
        assertNotEquals(restoredScreenB, screenB2)
    }
}