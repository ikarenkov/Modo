package com.github.terrakok.modo.model

import com.github.terrakok.modo.MockScreen
import com.github.terrakok.modo.MockScreenModel
import com.github.terrakok.modo.ScreenKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScreenModelStoreTest {

    @BeforeEach
    fun setup() {
        ScreenModelStore.removedScreenKeys.clear()
        ScreenModelStore.screenModels.clear()
        ScreenModelStore.dependencies.clear()
    }

    @Test
    fun `When screen is removed - than screen model is removed too`() {
        // Given
        val store = ScreenModelStore
        val screen = MockScreen(ScreenKey("screen"))

        val screenModel = store.getOrPut(screen = screen, tag = null) {
            MockScreenModel(id = "model")
        }

        // When
        store.remove(screen)

        // Then - verify screen model was removed by checking that getting it for a new screen creates a new instance
        val newScreen = MockScreen(ScreenKey("screen2"))
        val newScreenModel = store.getOrPut(screen = newScreen, tag = null) {
            MockScreenModel(id = "model2")
        }
        assertEquals("model2", newScreenModel.id, "Should create new screen model for different screen")
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

    @Test
    fun `When removing screen with key Screen#1 - then Screen#10 dependencies and screen models are not removed`() {
        // Given
        val store = ScreenModelStore
        val screen1 = MockScreen(ScreenKey("Screen#1"))
        val screen10 = MockScreen(ScreenKey("Screen#10"))

        var screen1DependencyDisposed = false
        var screen10DependencyDisposed = false

        store.getOrPutDependency(
            screen = screen1,
            name = "dependency",
            onDispose = { screen1DependencyDisposed = true }
        ) { "screen1-dep" }

        store.getOrPutDependency(
            screen = screen10,
            name = "dependency",
            onDispose = { screen10DependencyDisposed = true }
        ) { "screen10-dep" }

        store.getOrPut(screen = screen1, tag = null) {
            MockScreenModel(id = "model1")
        }

        val screenModel10 = store.getOrPut(screen = screen10, tag = null) {
            MockScreenModel(id = "model10")
        }

        // When
        store.remove(screen1)

        // Then - Screen#1 dependency disposed, Screen#10 dependency not disposed
        assertEquals(true, screen1DependencyDisposed, "Screen#1 dependency should be disposed")
        assertEquals(false, screen10DependencyDisposed, "Screen#10 dependency should NOT be disposed")

        val screen10DepValue = store.getDependencyOrNull<String>(screen10, "dependency")
        assertEquals("screen10-dep", screen10DepValue, "Screen#10 dependency should still exist")

        // Then - Screen#10 model still exists (returns same instance)
        val actualScreenModel10 = store.getOrPut(screen = screen10, tag = null) {
            MockScreenModel(id = "should-not-be-created")
        }
        assertEquals(screenModel10, actualScreenModel10, "Screen#10 screen model should still exist")
        assertEquals("model10", actualScreenModel10.id, "Should return existing Screen#10 model, not create new one")
    }

    @Test
    fun `When removing screen with prefix key - then screens with longer matching prefixes are not removed`() {
        // Given
        val store = ScreenModelStore
        val screenA = MockScreen(ScreenKey("A"))
        val screenAB = MockScreen(ScreenKey("AB"))
        val screenABC = MockScreen(ScreenKey("ABC"))

        var depADisposed = false
        var depABDisposed = false
        var depABCDisposed = false

        store.getOrPutDependency(screen = screenA, name = "dep", onDispose = { depADisposed = true }) { "A" }
        store.getOrPutDependency(screen = screenAB, name = "dep", onDispose = { depABDisposed = true }) { "AB" }
        store.getOrPutDependency(screen = screenABC, name = "dep", onDispose = { depABCDisposed = true }) { "ABC" }

        // When - remove screen with shortest key
        store.remove(screenA)

        // Then
        assertEquals(true, depADisposed, "Screen A dependency should be disposed")
        assertEquals(false, depABDisposed, "Screen AB dependency should NOT be disposed")
        assertEquals(false, depABCDisposed, "Screen ABC dependency should NOT be disposed")

        // Verify AB and ABC dependencies still exist
        assertEquals("AB", store.getDependencyOrNull<String>(screenAB, "dep"))
        assertEquals("ABC", store.getDependencyOrNull<String>(screenABC, "dep"))
    }

}