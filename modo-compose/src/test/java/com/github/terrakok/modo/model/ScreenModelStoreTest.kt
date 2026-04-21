package com.github.terrakok.modo.model

import com.github.terrakok.modo.MockScreen
import com.github.terrakok.modo.MockScreenModel
import com.github.terrakok.modo.ModoDevOptions
import com.github.terrakok.modo.ScreenKey
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ScreenModelStoreTest {

    @BeforeEach
    fun setup() {
        ScreenModelStore.removedScreenKeys.clear()
        ScreenModelStore.screenModels.clear()
        ScreenModelStore.dependencies.clear()
        ScreenModelStore.dependencyCounter.set(0L)
        ScreenModelStore.lastScreenModelKey.value = null

        // Reset validation strategy to default (log only, don't throw)
        ModoDevOptions.onIllegalScreenModelStoreAccess = ModoDevOptions.ValidationFailedStrategy { }
    }

    @Test
    fun `When get screen model repeatedly on the same screen - Then returns the same one`() {
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
    fun `When removing screen with key Screen#1 - Then Screen#10 dependencies and screen models are not removed`() {
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
    fun `When removing screen with prefix key - Then screens with longer matching prefixes are not removed`() {
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

    @Test
    fun `When removing screen with multiple dependencies - Then dependencies are removed in order of creation`() {
        // Given
        val store = ScreenModelStore
        val screen = MockScreen(ScreenKey("screen"))
        val disposalOrder = mutableListOf<String>()

        // Create dependencies in specific order
        store.getOrPutDependency(
            screen = screen,
            name = "dep1",
            onDispose = { disposalOrder.add("dep1") }
        ) { "value1" }

        store.getOrPutDependency(
            screen = screen,
            name = "dep2",
            onDispose = { disposalOrder.add("dep2") }
        ) { "value2" }

        store.getOrPutDependency(
            screen = screen,
            name = "dep3",
            onDispose = { disposalOrder.add("dep3") }
        ) { "value3" }

        // When
        store.remove(screen)

        // Then - dependencies should be disposed in order of creation (by removePriority)
        assertEquals(listOf("dep1", "dep2", "dep3"), disposalOrder, "Dependencies should be removed in creation order")
    }

    @Test
    fun `When removing screen - Then screen model removed and onDispose is called`() {
        // Given
        val store = ScreenModelStore
        val screen = MockScreen(ScreenKey("screen"))
        var onDisposeCalled = false

        val screenModel = MockScreenModel {
            onDisposeCalled = true
        }

        store.getOrPut(screen = screen, tag = null) { screenModel }

        // When
        store.remove(screen)

        // Then
        assertEquals(true, onDisposeCalled, "Screen model onDispose should be called")

        assertEquals(
            expected = null,
            actual = store.getOrNull<MockScreenModel>(screen = screen, tag = null),
            message = "Screen model should be removed from store"
        )
        assertTrue(store.screenModels.isEmpty(), "ScreenModelStore should be empty after removing the only screen")
    }

    @Test
    fun `When screen has multiple tagged screen models - Then each can be retrieved independently`() {
        // Given
        val store = ScreenModelStore
        val screen = MockScreen(ScreenKey("screen"))

        val model1 = store.getOrPut(screen = screen, tag = "tag1") {
            MockScreenModel(id = "model1")
        }

        val model2 = store.getOrPut(screen = screen, tag = "tag2") {
            MockScreenModel(id = "model2")
        }

        val modelDefault = store.getOrPut(screen = screen, tag = null) {
            MockScreenModel(id = "modelDefault")
        }

        // When - retrieve them again
        val retrievedModel1 = store.getOrPut(screen = screen, tag = "tag1") {
            MockScreenModel(id = "should-not-create")
        }

        val retrievedModel2 = store.getOrPut(screen = screen, tag = "tag2") {
            MockScreenModel(id = "should-not-create")
        }

        val retrievedModelDefault = store.getOrPut(screen = screen, tag = null) {
            MockScreenModel(id = "should-not-create")
        }

        // Then
        assertEquals(model1, retrievedModel1, "Tagged model1 should be retrieved")
        assertEquals(model2, retrievedModel2, "Tagged model2 should be retrieved")
        assertEquals(modelDefault, retrievedModelDefault, "Default tagged model should be retrieved")
        assertEquals("model1", retrievedModel1.id)
        assertEquals("model2", retrievedModel2.id)
        assertEquals("modelDefault", retrievedModelDefault.id)
    }

    @Test
    fun `When removing screen with tagged screen models - Then all are removed`() {
        // Given
        val store = ScreenModelStore
        val screen = MockScreen(ScreenKey("screen"))

        var model1Disposed = false
        var model2Disposed = false
        var modelDefaultDisposed = false

        store.getOrPut(screen = screen, tag = "tag1") {
            MockScreenModel {
                model1Disposed = true
            }
        }

        store.getOrPut(screen = screen, tag = "tag2") {
            MockScreenModel {
                model2Disposed = true
            }
        }

        store.getOrPut(screen = screen, tag = null) {
            MockScreenModel {
                modelDefaultDisposed = true
            }
        }

        // When
        store.remove(screen)

        // Then
        assertEquals(true, model1Disposed, "Tagged model1 should be disposed")
        assertEquals(true, model2Disposed, "Tagged model2 should be disposed")
        assertEquals(true, modelDefaultDisposed, "Default tagged model should be disposed")
    }

    @Test
    fun `When removing same screen twice - Then validation error is reported`() {
        // Given
        val store = ScreenModelStore
        val screen = MockScreen(ScreenKey("screen"))
        var validationErrorCaught: Throwable? = null

        store.getOrPut(screen = screen, tag = null) {
            MockScreenModel(id = "model")
        }

        // Set up validation handler to capture error
        ModoDevOptions.onIllegalScreenModelStoreAccess = ModoDevOptions.ValidationFailedStrategy { throwable ->
            validationErrorCaught = throwable
        }

        // When
        store.remove(screen)
        store.remove(screen) // Second removal

        // Then
        assertNotNull(validationErrorCaught, "Validation error should be reported")
        assertTrue(
            validationErrorCaught is IllegalStateException,
            "Error should be IllegalStateException"
        )
        assertTrue(
            validationErrorCaught?.message?.contains("Trying to remove screen") == true,
            "Error message should mention trying to remove screen"
        )
    }

    @Test
    fun `When trying to create screen model after screen removal - Then validation error is reported`() {
        // Given
        val store = ScreenModelStore
        val screen = MockScreen(ScreenKey("screen"))
        var validationErrorCaught: Throwable? = null

        // Create and remove screen
        store.getOrPut(screen = screen, tag = null) {
            MockScreenModel(id = "model")
        }
        store.remove(screen)

        // Set up validation handler to capture error
        ModoDevOptions.onIllegalScreenModelStoreAccess = ModoDevOptions.ValidationFailedStrategy { throwable ->
            validationErrorCaught = throwable
        }

        // When - try to create new screen model after removal
        store.getOrPut(screen = screen, tag = "newTag") {
            MockScreenModel(id = "should-not-create")
        }

        // Then
        assertNotNull(validationErrorCaught, "Validation error should be reported")
        assertTrue(
            validationErrorCaught is IllegalStateException,
            "Error should be IllegalStateException"
        )
        assertTrue(
            validationErrorCaught?.message?.contains("Trying to initialize dependency after screen is gone") == true,
            "Error message should mention initializing after screen is gone"
        )
    }

    @Test
    fun `When trying to create dependency after screen removal - Then validation error is reported`() {
        // Given
        val store = ScreenModelStore
        val screen = MockScreen(ScreenKey("screen"))
        var validationErrorCaught: Throwable? = null

        // Create and remove screen
        store.getOrPutDependency(
            screen = screen,
            name = "dep1",
            onDispose = {}
        ) { "value" }
        store.remove(screen)

        // Set up validation handler to capture error
        ModoDevOptions.onIllegalScreenModelStoreAccess = ModoDevOptions.ValidationFailedStrategy { throwable ->
            validationErrorCaught = throwable
        }

        // When - try to create new dependency after removal
        store.getOrPutDependency(
            screen = screen,
            name = "dep2",
            onDispose = {}
        ) { "should-not-create" }

        // Then
        assertNotNull(validationErrorCaught, "Validation error should be reported")
        assertTrue(
            validationErrorCaught is IllegalStateException,
            "Error should be IllegalStateException"
        )
        assertTrue(
            validationErrorCaught?.message?.contains("Trying to initialize dependency after screen is gone") == true,
            "Error message should mention initializing after screen is gone"
        )
    }

    @Test
    fun `When getting dependency scoped to screen model - Then it is created and retrievable`() {
        // Given
        val store = ScreenModelStore
        val screen = MockScreen(ScreenKey("screen"))
        val screenModel = store.getOrPut(screen = screen, tag = null) {
            MockScreenModel(id = "model")
        }

        // When
        val dependency = store.getOrPutDependency(
            screenModel = screenModel,
            name = "screenModelDep",
            onDispose = {}
        ) { "screenModelValue" }

        // Then
        assertEquals("screenModelValue", dependency, "Dependency should be created")

        // Retrieve again to verify it's cached
        val retrievedDependency = store.getOrPutDependency(
            screenModel = screenModel,
            name = "screenModelDep",
            onDispose = {}
        ) { "should-not-create" }

        assertEquals("screenModelValue", retrievedDependency, "Dependency should be retrieved from cache")
    }

    @Test
    fun `When screen with screen model is removed - Then screen model dependencies are also removed`() {
        // Given
        val store = ScreenModelStore
        val screen = MockScreen(ScreenKey("screen"))
        val screenModel = store.getOrPut(screen = screen, tag = null) {
            MockScreenModel(id = "model")
        }

        var screenModelDepDisposed = false
        store.getOrPutDependency(
            screenModel = screenModel,
            name = "screenModelDep",
            onDispose = { screenModelDepDisposed = true }
        ) { "value" }

        // When
        store.remove(screen)

        // Then
        assertEquals(true, screenModelDepDisposed, "Screen model dependency should be disposed")
    }

    @Test
    fun `When screen has multiple dependencies with different names and tags - Then all are managed independently`() {
        // Given
        val store = ScreenModelStore
        val screen = MockScreen(ScreenKey("screen"))

        // Create dependencies with different names
        store.getOrPutDependency(screen = screen, name = "dep1") { "value1" }
        store.getOrPutDependency(screen = screen, name = "dep2") { "value2" }
        store.getOrPutDependency(screen = screen, name = "dep3") { "value3" }

        // Create dependencies with same name but different tags
        store.getOrPutDependency(screen = screen, name = "lifecycle", tag = "tag1") { "lifecycle-tag1" }
        store.getOrPutDependency(screen = screen, name = "lifecycle", tag = "tag2") { "lifecycle-tag2" }
        store.getOrPutDependency(screen = screen, name = "lifecycle", tag = null) { "lifecycle-default" }

        // When - retrieve dependencies with different names
        val retrievedDep1 = store.getDependencyOrNull<String>(screen, "dep1")
        val retrievedDep2 = store.getDependencyOrNull<String>(screen, "dep2")
        val retrievedDep3 = store.getDependencyOrNull<String>(screen, "dep3")

        // When - retrieve dependencies with different tags
        val retrievedLifecycleTag1 = store.getDependencyOrNull<String>(screen, "lifecycle", "tag1")
        val retrievedLifecycleTag2 = store.getDependencyOrNull<String>(screen, "lifecycle", "tag2")
        val retrievedLifecycleDefault = store.getDependencyOrNull<String>(screen, "lifecycle", null)

        // Then - different names are independent
        assertEquals("value1", retrievedDep1)
        assertEquals("value2", retrievedDep2)
        assertEquals("value3", retrievedDep3)

        // Then - different tags are independent
        assertEquals("lifecycle-tag1", retrievedLifecycleTag1)
        assertEquals("lifecycle-tag2", retrievedLifecycleTag2)
        assertEquals("lifecycle-default", retrievedLifecycleDefault)
    }

    @Test
    fun `When getting dependency for screen that never had one - Then returns null`() {
        // Given
        val store = ScreenModelStore
        val screen = MockScreen(ScreenKey("screen"))

        // When
        val dependency = store.getDependencyOrNull<String>(screen, "nonexistent")

        // Then
        assertNull(dependency, "Non-existent dependency should return null")
    }

    @Test
    fun `When creating screen model - Then lastScreenModelKey is updated`() {
        // Given
        val store = ScreenModelStore
        val screen = MockScreen(ScreenKey("screen"))

        // Initial state
        assertNull(store.lastScreenModelKey.value, "Initially lastScreenModelKey should be null")

        // When
        store.getOrPut(screen = screen, tag = "myTag") {
            MockScreenModel(id = "model")
        }

        // Then
        assertNotNull(store.lastScreenModelKey.value, "lastScreenModelKey should be set after creating screen model")
        assertTrue(
            store.lastScreenModelKey.value?.contains("screen") == true,
            "lastScreenModelKey should contain screen key"
        )
        assertTrue(
            store.lastScreenModelKey.value?.contains("MockScreenModel") == true,
            "lastScreenModelKey should contain screen model class name"
        )
        assertTrue(
            store.lastScreenModelKey.value?.contains("myTag") == true,
            "lastScreenModelKey should contain tag"
        )
    }

    @Test
    fun `When removing screen with both screen models and dependencies - Then all are removed in correct order`() {
        // Given
        val store = ScreenModelStore
        val screen = MockScreen(ScreenKey("screen"))
        val disposalOrder = mutableListOf<String>()

        // Create screen model
        store.getOrPut(screen = screen, tag = null) {
            MockScreenModel {
                disposalOrder.add("screenModel")
            }
        }

        // Create dependencies
        store.getOrPutDependency(screen = screen, name = "dep1", onDispose = { disposalOrder.add("dep1") }) { "value1" }
        store.getOrPutDependency(screen = screen, name = "dep2", onDispose = { disposalOrder.add("dep2") }) { "value2" }

        // When
        store.remove(screen)

        // Then - screen model should be disposed first, then dependencies in order
        assertEquals(3, disposalOrder.size, "All items should be disposed")
        assertEquals("screenModel", disposalOrder[0], "Screen model should be disposed first")
        assertEquals(listOf("screenModel", "dep1", "dep2"), disposalOrder, "Items should be disposed in correct order")
    }

}