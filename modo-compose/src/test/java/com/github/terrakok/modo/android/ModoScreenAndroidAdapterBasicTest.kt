package com.github.terrakok.modo.android

import androidx.lifecycle.Lifecycle
import com.github.terrakok.modo.MockScreen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.android.ModoScreenAndroidAdapterTestUtils.cleanupArchTaskExecutor
import com.github.terrakok.modo.android.ModoScreenAndroidAdapterTestUtils.cleanupScreenModelStore
import com.github.terrakok.modo.android.ModoScreenAndroidAdapterTestUtils.setupArchTaskExecutor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

class ModoScreenAndroidAdapterBasicTest {

    private lateinit var screen: MockScreen
    private lateinit var adapter: ModoScreenAndroidAdapter

    @BeforeEach
    fun setup() {
        setupArchTaskExecutor()
        cleanupScreenModelStore()
        screen = MockScreen(ScreenKey("test-screen"))
        adapter = ModoScreenAndroidAdapter.get(screen)
    }

    @AfterEach
    fun tearDown() {
        cleanupArchTaskExecutor()
        cleanupScreenModelStore()
    }

    @Test
    fun `When adapter is created - Then lifecycle is in INITIALIZED state`() {
        assertEquals(Lifecycle.State.INITIALIZED, adapter.lifecycle.currentState)
    }

    // TODO: Add ModoScreenAndroidAdapterLifecycleTest for integration tests requiring Compose initialization:
    //  - showTransitionFinished, hideTransitionStarted, onPreDispose
    //  - parent lifecycle propagation
    //  - manualResumePause mode

    @Test
    fun `When adapter is created - Then viewModelStore is available`() {
        assertDoesNotThrow { adapter.viewModelStore }
    }

    @Test
    fun `When adapter is created - Then savedStateRegistry is available`() {
        assertDoesNotThrow { adapter.savedStateRegistry }
    }

    @Test
    fun `When adapter is created - Then defaultViewModelProviderFactory is available`() {
        assertDoesNotThrow { adapter.defaultViewModelProviderFactory }
    }
}
