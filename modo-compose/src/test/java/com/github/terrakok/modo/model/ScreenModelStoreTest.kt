package com.github.terrakok.modo.model

import com.github.terrakok.modo.MockScreen
import com.github.terrakok.modo.MockScreenModel
import com.github.terrakok.modo.ScreenKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ScreenModelStoreTest {

    @Test
    fun `When screen is removed - than screen model is removed too`() {
        // Given
        val store = ScreenModelStore
        val key = ScreenKey("screen")
        val screen = MockScreen(key)
        store.getOrPut(screen = screen, tag = null) {
            MockScreenModel()
        }

        // When
        store.remove(screen)

        // Then
        assertEquals(
            0,
            store.screenModels.size
        )
    }

    @Test
    fun `When get screen model repeatedly on the same screen - then returns the same one`() {
        // Given
        val store = ScreenModelStore
        val key = ScreenKey("screen")
        val screen = MockScreen(key)
        val screenModel = MockScreenModel(id = "1")
        store.getOrPut(screen = screen, tag = null) {
            screenModel
        }

        // When
        val actualScreenModel = store.getOrPut(screen = screen, tag = null) {
            MockScreenModel(id = "2")
        }

        assertEquals(screenModel, actualScreenModel)
    }
}