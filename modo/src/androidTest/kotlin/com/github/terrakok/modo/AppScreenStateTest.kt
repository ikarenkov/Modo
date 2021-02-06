package com.github.terrakok.modo

import org.junit.Test
import kotlin.test.assertEquals

class AppScreenStateTest {

    @Test
    fun serializationTest1() {
        val screen = AppScreen("A", true) {
            error("AppScreen stub!")
        }
        val restored = AppScreen.parse(screen.stringify())
        assertEquals(screen, restored)
    }

    @Test
    fun serializationTest2() {
        val screen = AppScreen("B", false) {
            error("AppScreen stub!")
        }
        val restored = AppScreen.parse(screen.stringify())
        assertEquals(screen, restored)
    }
}