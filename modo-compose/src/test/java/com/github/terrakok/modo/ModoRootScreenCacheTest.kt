package com.github.terrakok.modo

import android.os.Bundle
import com.github.terrakok.modo.model.ScreenModelStore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame

@Suppress("DEPRECATION")
class ModoRootScreenCacheTest {

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        Modo.rootScreens.clear()
        screenCounterKey.set(-1)
        ScreenModelStore.removedScreenKeys.clear()
        ScreenModelStore.screenModels.clear()
        ScreenModelStore.dependencies.clear()
        ScreenModelStore.dependencyCounter.set(0L)
        ScreenModelStore.lastScreenModelKey.value = null
        ModoDevOptions.onIllegalScreenModelStoreAccess = ModoDevOptions.ValidationFailedStrategy { }
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region Scenario 3: first initialization (savedState == null, inMemoryScreen == null)

    @Test
    fun `When savedState is null and inMemoryScreen is null - Then factory creates new and caches it`() {
        val mockScreen = MockScreen(ScreenKey("new"))

        val result = Modo.getOrCreateRootScreen<MockScreen>(
            savedState = null,
            inMemoryScreen = null,
            rootScreenProvider = { mockScreen }
        )

        assertSame(result, Modo.rootScreens[result.screenKey])
    }

    // endregion

    // region Scenario 2: in-memory hit (savedState == null, inMemoryScreen != null — fragment backstack return)

    @Test
    fun `When savedState is null and inMemoryScreen is provided - Then inMemoryScreen is returned`() {
        val existing = RootScreen(MockScreen())
        Modo.rootScreens[existing.screenKey] = existing

        val result = Modo.getOrCreateRootScreen(
            savedState = null,
            inMemoryScreen = existing,
            rootScreenProvider = { error("factory must not be called") }
        )

        assertSame(existing, result)
    }

    @Test
    fun `When savedState is null and inMemoryScreen is provided - Then factory is not called`() {
        val existing = RootScreen(MockScreen())
        Modo.rootScreens[existing.screenKey] = existing
        var factoryCalled = false

        Modo.getOrCreateRootScreen(
            savedState = null,
            inMemoryScreen = existing,
            rootScreenProvider = {
                factoryCalled = true
                MockScreen()
            }
        )

        assertSame(false, factoryCalled)
    }

    // endregion

    // region Scenario 1a: bundle restore, cache hit (config change — rootScreens has live instance)

    @Test
    fun `When savedState is not null and rootScreens has instance - Then cached instance returned, not bundle one`() {
        // Simulate config change: cached holds the live instance, bundle holds the parcelized one.
        // In practice they'd be equal by value but different objects; here we reuse the same instance
        // for the bundle to share the same screenKey, then verify the cache takes priority.
        val cached = RootScreen(MockScreen())
        Modo.rootScreens[cached.screenKey] = cached
        val savedState = mockBundle(cached, counter = 5)

        val result = Modo.getOrCreateRootScreen<MockScreen>(
            savedState = savedState,
            inMemoryScreen = null,
            rootScreenProvider = { error("factory must not be called") }
        )

        assertSame(cached, result)
    }

    // endregion

    // region Scenario 1b: bundle restore, cache miss (process death — rootScreens is empty)

    @Test
    fun `When savedState is not null and rootScreens is empty - Then bundle instance stored and returned`() {
        val fromBundle = RootScreen(MockScreen())
        screenCounterKey.set(-1) // reset: simulates new process after death, counter not yet set
        val savedState = mockBundle(fromBundle, counter = 42)

        val result = Modo.getOrCreateRootScreen<MockScreen>(
            savedState = savedState,
            inMemoryScreen = null,
            rootScreenProvider = { error("factory must not be called") }
        )

        assertSame(fromBundle, result)
        assertSame(fromBundle, Modo.rootScreens[fromBundle.screenKey])
    }

    @Test
    fun `When savedState is not null and rootScreens is empty - Then screenCounter is restored`() {
        val fromBundle = RootScreen(MockScreen())
        screenCounterKey.set(-1) // reset: simulates new process after death, counter not yet set
        val savedState = mockBundle(fromBundle, counter = 42)

        Modo.getOrCreateRootScreen<MockScreen>(
            savedState = savedState,
            inMemoryScreen = null,
            rootScreenProvider = { error("factory must not be called") }
        )

        assertSame(42, screenCounterKey.get())
    }

    // endregion

    // region getOrPut cache isolation

    @Test
    fun `When screen is cached and getOrPut called again - Then cached instance returned`() {
        val cached = RootScreen(MockScreen(ScreenKey("k")))
        Modo.rootScreens[ScreenKey("k")] = cached

        val newCandidate = RootScreen(MockScreen(ScreenKey("k")))

        @Suppress("UNCHECKED_CAST")
        val result = Modo.rootScreens.getOrPut(ScreenKey("k")) { newCandidate } as RootScreen<MockScreen>

        assertSame(cached, result)
        assertNotSame(newCandidate, result)
    }

    // endregion

    // region onRootScreenFinished cleanup

    @Test
    fun `When onRootScreenFinished called - Then rootScreens entry is removed`() {
        val screen = RootScreen(MockScreen(ScreenKey("fin")))
        Modo.rootScreens[screen.screenKey] = screen

        Modo.onRootScreenFinished(screen)

        assertNull(Modo.rootScreens[screen.screenKey])
    }

    // endregion

    private fun mockBundle(rootScreen: RootScreen<*>, counter: Int): Bundle = mockk {
        every { getParcelable<RootScreen<*>>("MODO_GRAPH") } returns rootScreen
        every { getInt("MODO_SCREEN_COUNTER_KEY") } returns counter
    }
}
